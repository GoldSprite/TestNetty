package goldsprite.myUdpNetty.handlers;

import goldsprite.myUdpNetty.codec.codecInterfaces.IStatus;
import goldsprite.myUdpNetty.codec.packets.*;
import goldsprite.myUdpNetty.other.PacketCallback2;
import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import goldsprite.myUdpNetty.starter.UdpServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.util.HashMap;
import java.util.stream.Collectors;

import lombok.var;

public class PacketsHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private boolean isServer;
    private HashMap<Class<? extends Packet>, PacketCallback2> callbacks = new HashMap<>();

    public PacketsHandler() {
    }

    public PacketsHandler(boolean isServer) {
        this.isServer = isServer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("频道激活.");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println(ctx.name() + ": CustomPacketHandler.channelReadComplete");
        ctx.channel().flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket dpk) throws Exception {
//        System.out.println(ctx.name() + ": CustomPacketHandler.channelRead");
        var sender = dpk.sender();
        var packet = goldsprite.myUdpNetty.codec.PacketCodeC.INSTANCE.decode(dpk.content());

        if(isServer){
            var intercept = strangerInterceptor(packet);
            if (intercept){
                System.out.println("已拦截未登录者消息.");
                return;
            }
        }

        switch (packet.getCommand()) {
            case ICommand.LOGIN_REQUEST: {
                LoginRequestPacket pk = (LoginRequestPacket) packet;
                handleLoginRequestPacket(pk);
                break;
            }
            case ICommand.LOGIN_RESPONSE: {
                LoginResponsePacket pk = (LoginResponsePacket) packet;
                handleLoginResponsePacket(pk);
                break;
            }
            case ICommand.MESSAGE_REQUEST: {
                MessageRequestPacket pk = (MessageRequestPacket) packet;
                handleMessageRequestPacket(pk);
                break;
            }
            case ICommand.MESSAGE_RESPONSE: {
                MessageResponsePacket pk = (MessageResponsePacket) packet;
                handleMessageResponsePacket(pk);
                break;
            }
            default: break;
        }

    }

    private void handleMessageResponsePacket(MessageResponsePacket pk) {
    }

    private void handleMessageRequestPacket(MessageRequestPacket pk) {
    }

    private void handleLoginResponsePacket(LoginResponsePacket pk) {
    }

    private void handleLoginRequestPacket(LoginRequestPacket pk) {
    }

    //拦截非登录用户包(除正在登录)
    private boolean strangerInterceptor(Packet packet) {
        if(packet instanceof LoginRequestPacket) return false;
        
        var playerGuid = packet.getOwnerGuid();
        return !UdpServer.Instance.isOnline(playerGuid);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("处理器异常: ");
        cause.printStackTrace();
    }

    private String isValid(LoginRequestPacket loginpk) {
        if (UdpServer.Instance.clients.containsKey(loginpk.getOwnerGuid())) {
            return "您已经登录.";
        }

        return "";
    }

    private boolean isValid(MoveRequestPacket movpk) {
        return true;
    }

    public <T extends Packet> void addCallbackListener(Class<? extends Packet> ppid, PacketCallback2<T> callback) {
        callbacks.put(ppid, pk -> {
//            System.out.println("插入空代理...");
            callback.callback((T) pk);
        });
    }

    private <T extends Packet> void callback(T pk) {
        var callback = callbacks.get(pk.getClass());
        if (callback != null) {
            callback.callback(pk);
        }
    }
}

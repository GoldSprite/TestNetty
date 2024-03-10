package goldsprite.myUdpNetty.handlers;

import goldsprite.myUdpNetty.codec.codecInterfaces.IStatus;
import goldsprite.myUdpNetty.codec.packets.*;
import goldsprite.myUdpNetty.other.PacketCallback2;
import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import goldsprite.myUdpNetty.starter.UdpServer;
import goldsprite.myUdpNetty.tools.LogTools;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import lombok.var;

public class PacketsHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private boolean isServer;
    private HashMap<Class<? extends Packet>, List<PacketCallback2>> callbacks = new HashMap<>();

    public PacketsHandler() {
    }

    public PacketsHandler(boolean isServer) {
        this.isServer = isServer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        LogTools.NLog("频道激活.");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        LogTools.NLog(ctx.name() + ": CustomPacketHandler.channelReadComplete");
        ctx.channel().flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket dpk) throws Exception {
//        LogTools.NLog(ctx.name() + ": CustomPacketHandler.channelRead");
        var sender = dpk.sender();
        var packet = goldsprite.myUdpNetty.codec.PacketCodeC.INSTANCE.decode(dpk.content());

        LogTools.NLog("收到包类型: "+packet.getClass().getSimpleName());
//        if(strangerIntercept)
//            if(isServer){
//                var intercept = strangerInterceptor(packet);
//                if (intercept){
//                    LogTools.NLog("已拦截未登录者消息.");
//                    return;
//                }
//            }

        switch (packet.getCommand()) {
            case ICommand.LOGIN_REQUEST: {
                LoginRequestPacket pk = (LoginRequestPacket) packet;
                handleLoginRequestPacket(pk, sender);
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
            default:
                break;
        }

        if (IStatus.isReturnStatus(packet.getCode()))
            callback(packet);

    }

    private void handleMessageResponsePacket(MessageResponsePacket pk) {
    }

    private void handleMessageRequestPacket(MessageRequestPacket pk) {
        var rep = new MessageResponsePacket(pk.getOwnerGuid(), IStatus.RETURN_SUCCESS);
        UdpServer.Instance.sendPacket(rep);
    }

    private void handleLoginResponsePacket(LoginResponsePacket pk) {
    }

    private void handleLoginRequestPacket(LoginRequestPacket pk, InetSocketAddress sender) {
        var status = loginAuthentication(pk);
        var newGuid = UdpServer.Instance.endGuid++;
        UdpServer.Instance.loginClient(pk, sender, newGuid);

        var rep = new LoginResponsePacket(newGuid, status);
        UdpServer.Instance.sendPacket(rep);
    }

    private int loginAuthentication(LoginRequestPacket pk) {
        var statusId = IStatus.RETURN_SUCCESS;
        if (UdpServer.Instance.isOnline(pk.getOwnerGuid())) {
            LogTools.NLog(IStatus.msgMap.get(IStatus.RETURN_DEFEAT_LOGIN_REPEAT));
            return IStatus.RETURN_DEFEAT_LOGIN_REPEAT;
        }
        return statusId;
    }

//    //拦截非登录用户包(除正在登录)
//    private boolean strangerInterceptor(Packet packet) {
//        if(packet instanceof LoginRequestPacket) return false;
//
//        var playerGuid = packet.getOwnerGuid();
//        return !UdpServer.Instance.isOnline(playerGuid);
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogTools.NLog("处理器异常: ");
        cause.printStackTrace();
    }

//    private String isValid(LoginRequestPacket loginpk) {
//        if (UdpServer.Instance.clients.containsKey(loginpk.getOwnerGuid())) {
//            return "您已经登录.";
//        }
//
//        return "";
//    }
//
//    private boolean isValid(MoveRequestPacket movpk) {
//        return true;
//    }

    public <T extends Packet> void addCallbackListener(Class<? extends Packet> ppid, PacketCallback2<T> callback) {
        if(!callbacks.containsKey(ppid)) callbacks.put(ppid, new ArrayList<>());
        var pkCallbacks = callbacks.get(ppid);
        pkCallbacks.add((pk) -> {
            LogTools.NLog(IStatus.getStatusMsg(pk));
            callback.callback((T) pk);
        });
    }

    private <T extends Packet> void callback(T pk) {
        var pkCallbacks = callbacks.get(pk.getClass());
        if (pkCallbacks.size() > 0) {
            pkCallbacks.forEach((p)->p.callback(pk));
            pkCallbacks.clear();
        }
    }
}

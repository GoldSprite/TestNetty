package goldsprite.testNetty3_Udp;

import goldsprite.packets.ICommand;
import goldsprite.packets.MyPackets.*;
import goldsprite.packets.PacketCodeC;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.util.HashMap;
import java.util.stream.Collectors;

public class CustomPacketHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private final boolean isServer;
    private HashMap<String, UdpClient.PacketCallback> callbacks = new HashMap<>();
    public String ClientName = "Client";

    public CustomPacketHandler(boolean isServer) {
        this.isServer = isServer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("频道激活.");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.name() + ": CustomPacketHandler.channelReadComplete");
        ctx.channel().flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket dpk) throws Exception {
        System.out.println(ctx.name() + ": CustomPacketHandler.channelRead");
        var sender = dpk.sender();
        var packet = PacketCodeC.INSTANCE.decode(dpk.content());

        switch (packet.getCommand()) {
            case ICommand.LOGIN_REQUEST:
                var loginpk = (LoginRequestPacket) packet;
                System.out.println("收到登陆包 "+loginpk.toString());
                //回包
                if(isValid(loginpk)){
                    System.out.println("验证成功");
                    Server.Instance.addClient(this);
                    var loginPkRep = new LoginResponsePacket();
                    loginPkRep.setSuccess(true);
                    var dpkRep = PacketCodeC.INSTANCE.encodeDpk(ctx.alloc(), loginPkRep, sender);
                    ctx.channel().writeAndFlush(dpkRep);
                }else{
                    System.out.println("验证失败.");
                }
                break;

            case ICommand.LOGIN_RESPONSE:
                var loginreppk = (LoginResponsePacket) packet;
                if(loginreppk.isSuccess()){
                    System.out.println("登录成功.");
                }else{
                    System.out.println("登陆失败.");
                }
                break;

            case ICommand.MOVE_REQUEST:
                var movpk = (MoveRequestPacket) packet;
                System.out.println("收到移动包 "+movpk.toString());
                //回包
                if(isValid(movpk)){
                    System.out.println("验证成功");
                    var movpkrep = new MoveResponsePacket();
                    movpkrep.setSuccess(true);
                    var dpkRep = PacketCodeC.INSTANCE.encodeDpk(ctx.alloc(), movpkrep, sender);
                    ctx.channel().writeAndFlush(dpkRep);
                }else{
                    System.out.println("验证失败.");
                }
                break;

            case ICommand.MOVE_RESPONSE:
                var movreppk = (MoveResponsePacket) packet;
                if(movreppk.isSuccess()){
                    System.out.println("移动成功.");
                }else{
                    System.out.println("移动失败.");
                }
                break;

            case ICommand.QUERYROOMINFO_REQUEST:
                var qrypk = (QueryRoomInfoPacket) packet;
                System.out.println("收到查询包 "+qrypk.toString());
                //回包
                if(true){
                    System.out.println("验证成功");
                    var qrypkrep = new QueryRoomInfoResponsePacket(
                            true,
                            "",
                            qrypk.getPpid(),
                            Server.Instance.clientCount(),
                            4,
                            Server.Instance.clients.stream().map(p->p.ClientName).collect(Collectors.toList()).toArray(new String[]{})
                    );
                    var dpkRep = PacketCodeC.INSTANCE.encodeDpk(ctx.alloc(), qrypkrep, sender);
                    ctx.channel().writeAndFlush(dpkRep);
                }else{
                    System.out.println("验证失败.");
                }
                break;

            case ICommand.QUERYROOMINFO_RESPONSE:
                var qryreppk = (QueryRoomInfoResponsePacket) packet;
                if(callbacks.containsKey(qryreppk.getPpid())){
                    callbacks.get(qryreppk.getPpid()).callback(qryreppk);
                    callbacks.remove(qryreppk.getPpid());
                }
                if(qryreppk.isSuccess()){
                    System.out.println("查询成功.");
                }else{
                    System.out.println("查询失败.");
                }
                break;

            default: break;
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(getClass().getSimpleName()+"处理器异常: "+cause);
    }

    private boolean isValid(LoginRequestPacket loginpk) {
        return true;
    }

    private boolean isValid(MoveRequestPacket movpk) {
        return true;
    }

    public void addCallbackListener(UdpClient.PacketCallback callback) {
        callbacks.put(callback.ppid, callback);
    }
}

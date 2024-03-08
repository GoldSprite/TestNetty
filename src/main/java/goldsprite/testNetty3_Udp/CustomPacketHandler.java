package goldsprite.testNetty3_Udp;

import goldsprite.packets.ICommand;
import goldsprite.packets.MyPackets.*;
import goldsprite.packets.PacketCodeC;
import goldsprite.testNetty3_Udp.other.PacketCallback;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.util.HashMap;
import java.util.stream.Collectors;

public class CustomPacketHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private final boolean isServer;
    private HashMap<String, PacketCallback> callbacks = new HashMap<>();
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
                    var newGuid = Server.Instance.endGuid;
                    if(Server.Instance.addClient(loginpk, sender, newGuid)){
                        Server.Instance.endGuid++;
                        var loginPkRep = new LoginResponsePacket();
                        loginPkRep.setSuccess(true);
                        loginPkRep.setOwnerGuid(newGuid);
                        var dpkRep = PacketCodeC.INSTANCE.encodeDpk(ctx.alloc(), loginPkRep, sender);
                        ctx.channel().writeAndFlush(dpkRep);
                    }
                }
                break;

            case ICommand.LOGIN_RESPONSE:
                var loginreppk = (LoginResponsePacket) packet;
                if(loginreppk.isSuccess()){
                    System.out.println("登录成功.");
                    UdpClient.Instance.ownerGuid = loginreppk.getOwnerGuid();
                    UdpClient.Instance.startHeartBeatThread();

                    var callback = callbacks.get(loginreppk.getPpid());
                    if(callback != null){
                        callback.callback(loginreppk);
                        callbacks.remove(loginreppk.getPpid());
                    }
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
                            Server.Instance.clients.values().stream().map(p->p.name).collect(Collectors.toList()).toArray(new String[]{})
                    );
                    var dpkRep = PacketCodeC.INSTANCE.encodeDpk(ctx.alloc(), qrypkrep, sender);
                    ctx.channel().writeAndFlush(dpkRep);
                }else{
                    System.out.println("验证失败.");
                }
                break;

            case ICommand.QUERYROOMINFO_RESPONSE:
                var qryreppk = (QueryRoomInfoResponsePacket) packet;
                if(qryreppk.isSuccess()){
                    System.out.println("查询成功.");
                    var callback = callbacks.get(qryreppk.getPpid());
                    if(callback != null){
                        callback.callback(qryreppk);
                        callbacks.remove(qryreppk.getPpid());
                    }
                }else{
                    System.out.println("查询失败.");
                }
                break;

            case ICommand.HEARTBEAT_REQUEST:
                var hpk = (HeartBeatRequestPacket) packet;
                var valid = Server.Instance.clients.containsKey(hpk.getOwnerGuid());
                if(valid){
                    var client = Server.Instance.clients.get(hpk.getOwnerGuid());
                    //刷新心跳
                    client.afkHearts = System.currentTimeMillis()+Server.heartTicker;
                    System.out.println("验证成功, 收到心跳包 ["+hpk.getOwnerGuid()+"-"+client.name+"-"+client.address+"], 刷新心跳: "+client.afkHearts);

                    //回跳包
                    var hpkrep = new HeartBeatResponsePacket(true, "", hpk.getPpid(), System.currentTimeMillis());
                    var dpkRep = PacketCodeC.INSTANCE.encodeDpk(ctx.alloc(), hpkrep, sender);
                    ctx.channel().writeAndFlush(dpkRep);
                }else{
                    System.out.println("心跳包验证失败, 查无此人.");
                }
                break;

            case ICommand.HEARTBEAT_RESPONSE:
                var hreppk = (HeartBeatResponsePacket) packet;
                if(hreppk.isSuccess()){
                    System.out.println("心跳成功.");
                    var callback = callbacks.get(hreppk.getPpid());
                    if(callback != null){
                        callback.callback(hreppk);
                        callbacks.remove(hreppk.getPpid());
                    }
                }else{
                    System.out.println("心跳失败.");
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
        if(Server.Instance.clients.containsKey(loginpk.getOwnerGuid())){
            System.out.println("您已经登录.");
            return false;
        }

        System.out.println("登录成功.");
        return true;
    }

    private boolean isValid(MoveRequestPacket movpk) {
        return true;
    }

    public void addCallbackListener(PacketCallback callback) {
        callbacks.put(callback.ppid, callback);
    }
}

package goldsprite.myUdpNetty.handlers;

import goldsprite.myUdpNetty.other.PacketCallback2;
import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;
import goldsprite.myUdpNetty.codec.packets.MoveRequestPacket;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import goldsprite.myUdpNetty.codec.PacketCodeC;
import goldsprite.myUdpNetty.starter.UdpClient;
import goldsprite.myUdpNetty.starter.UdpServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.util.HashMap;
import java.util.stream.Collectors;

import lombok.var;

import static goldsprite.myUdpNetty.starter.UdpServer.enableHeartBeats;

public class PacketsHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private boolean isServer;
    private HashMap<String, PacketCallback2> callbacks = new HashMap<>();

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

        handlerLoginPacket();

        switch (packet.getCommand()) {
            case ICommand.LOGIN_REQUEST: {
                var pk = (goldsprite.myUdpNetty.codec.packets.LoginRequestPacket) packet;
                switch (pk.getCode()) {
                    case ICommand.SEND_REQUEST: {
                        System.out.println("处理器收到客户端登陆包 " + pk.toString());
                        //回包
                        var unValidMes = isValid(pk);
                        if (unValidMes.equals("")) {
                            var newGuid = UdpServer.Instance.endGuid;
                            if (UdpServer.Instance.addClient(pk, sender, newGuid)) {
                                UdpServer.Instance.endGuid++;
                                pk.setCode(ICommand.RETURN_SUCCESS);
                                pk.setOwnerGuid(newGuid);
                            }
                        } else {
                            pk.setCode(ICommand.RETURN_DEFEAT);
                            pk.setReason(unValidMes);
                        }
                        UdpServer.Instance.sendPacket(pk);
                        System.out.println("处理器验证" + (unValidMes.equals("") ? "成功" : "失败"));
                        break;
                    }
                    case ICommand.RETURN_SUCCESS:
                    case ICommand.RETURN_DEFEAT: {
                        System.out.println("处理器收到服务端登录响应包: " + pk.toString());
                        var isSuccess = pk.getCode().equals(ICommand.RETURN_SUCCESS);
                        if (isSuccess) {
                            UdpClient.Instance.ownerGuid = pk.getOwnerGuid();  //获取绑定id
                            if(enableHeartBeats)
                                UdpClient.Instance.startHeartBeatThread();  //开启心跳线程
                        }
                        System.out.println("处理器验证" + (isSuccess ? "成功" : "失败"));
                        callback(pk);
                        callbacks.remove(pk.getPpid());
                        break;
                    }
                }
                break;
            }

            case ICommand.MOVE_REQUEST: {
                var movpk = (goldsprite.myUdpNetty.codec.packets.MoveRequestPacket) packet;
                System.out.println("收到移动包 " + movpk.toString());
                //回包
                if (isValid(movpk)) {
                    System.out.println("验证成功");
                    var movpkrep = new goldsprite.myUdpNetty.codec.packets.MoveResponsePacket();
                    movpkrep.setSuccess(true);
                    movpkrep.setOwnerGuid(movpk.getOwnerGuid());
                    movpkrep.setPpid(movpk.getPpid());
                    UdpServer.Instance.sendPacket(movpkrep);
                } else {
                    System.out.println("验证失败.");
                }
                break;
            }

            case ICommand.MOVE_RESPONSE: {
                var movreppk = (goldsprite.myUdpNetty.codec.packets.MoveResponsePacket) packet;
                callbacks.get(movreppk.getPpid()).callback(movreppk);
                if (movreppk.isSuccess()) {
                    System.out.println("移动成功.");
                } else {
                    System.out.println("移动失败.");
                }
                break;
            }

            case ICommand.QUERYROOMINFO_REQUEST: {
                var qrypk = (goldsprite.myUdpNetty.codec.packets.QueryRoomInfoPacket) packet;
                System.out.println("收到查询包 " + qrypk.toString());
                //回包
                if (true) {
                    System.out.println("验证成功");
                    var qrypkrep = new goldsprite.myUdpNetty.codec.packets.QueryRoomInfoResponsePacket(
                            true,
                            "",
                            qrypk.getPpid(),
                            UdpServer.Instance.clientCount(),
                            4,
                            UdpServer.Instance.clients.values().stream().map(p -> p.name).collect(Collectors.toList()).toArray(new String[]{})
                    );
                    var dpkRep = PacketCodeC.INSTANCE.encodeDpk(ctx.alloc(), qrypkrep, sender);
                    ctx.channel().writeAndFlush(dpkRep);
                } else {
                    System.out.println("验证失败.");
                }
                break;
            }

            case ICommand.QUERYROOMINFO_RESPONSE: {
                var qryreppk = (goldsprite.myUdpNetty.codec.packets.QueryRoomInfoResponsePacket) packet;
                if (qryreppk.isSuccess()) {
                    System.out.println("查询成功.");
                    var callback = callbacks.get(qryreppk.getPpid());
                    if (callback != null) {
                        callback.callback(qryreppk);
                        callbacks.remove(qryreppk.getPpid());
                    }
                } else {
                    System.out.println("查询失败.");
                }
                break;
            }

            case ICommand.HEARTBEAT_REQUEST: {
                var hpk = (goldsprite.myUdpNetty.codec.packets.HeartBeatRequestPacket) packet;
                switch (hpk.getCode()) {
                    case ICommand.SEND_REQUEST: {
                        var valid = UdpServer.Instance.clients.containsKey(hpk.getOwnerGuid());
                        if (valid) {
                            var client = UdpServer.Instance.clients.get(hpk.getOwnerGuid());
                            client.afkHearts = System.currentTimeMillis() + UdpServer.heartTicker;  //刷新心跳
                            hpk.setCode(ICommand.RETURN_SUCCESS);
//                            System.out.println("处理器验证成功, 收到客户端心跳包 [" + hpk.getOwnerGuid() + "-" + client.name + "-" + client.address + "], 刷新心跳: " + DateTools.formatTime(client.afkHearts));
                        } else {
                            hpk.setCode(ICommand.RETURN_DEFEAT);
                            var reason = "该玩家不在在线列表(已断线).";
                            hpk.setReason(reason);
                            System.out.println("处理器心跳包验证失败: " + reason);
                        }

                        //回跳包
                        hpk.setHeartMillis(System.currentTimeMillis());
                        UdpServer.Instance.sendPacket(hpk);
                        break;
                    }
                    case ICommand.RETURN_SUCCESS:
                    case ICommand.RETURN_DEFEAT: {
//                        System.out.println("处理器收到服务端心跳响应包: " + hpk.toString());
                        var isSuccess = hpk.getCode().equals(ICommand.RETURN_SUCCESS);
                        if(isSuccess){
                        }
//                        System.out.println("处理器验证" + (isSuccess ? "成功" : "失败"));
                        callback(hpk);
                        callbacks.remove(hpk.getPpid());
                        break;
                    }
                }
            }

            default:
                break;
        }

    }

    private void handlerLoginPacket() {

    }

    //拦截非登录用户包(除正在登录)
    private boolean strangerInterceptor(Packet packet) {
        if(packet instanceof goldsprite.myUdpNetty.codec.packets.LoginRequestPacket) return false;
        
        var playerGuid = packet.getOwnerGuid();
        return !UdpServer.Instance.isOnline(playerGuid);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("处理器异常: ");
        cause.printStackTrace();
    }

    private String isValid(goldsprite.myUdpNetty.codec.packets.LoginRequestPacket loginpk) {
        if (UdpServer.Instance.clients.containsKey(loginpk.getOwnerGuid())) {
            return "您已经登录.";
        }

        return "";
    }

    private boolean isValid(MoveRequestPacket movpk) {
        return true;
    }

    public <T extends Packet> void addCallbackListener(String ppid, PacketCallback2<T> callback) {
        callbacks.put(ppid, pk -> {
//            System.out.println("插入空代理...");
            callback.callback((T) pk);
        });
    }

    private <T extends Packet> void callback(T pk) {
        var callback = callbacks.get(pk.getPpid());
        if (callback != null) {
            callback.callback(pk);
        }
    }
}

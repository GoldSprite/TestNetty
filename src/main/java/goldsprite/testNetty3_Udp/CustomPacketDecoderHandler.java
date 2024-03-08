package goldsprite.testNetty3_Udp;


import goldsprite.testNetty.samples.packets.ICommand;
import goldsprite.testNetty.samples.packets.LoginRequestPacket;
import goldsprite.testNetty.samples.packets.LoginResponsePacket;
import goldsprite.testNetty.samples.packets.PacketCodeC;
import goldsprite.testNetty3_Udp.codec.UdpMyPacketCodeC;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

public class CustomPacketDecoderHandler extends ChannelInboundHandlerAdapter {
    private final boolean isServer;

    public CustomPacketDecoderHandler(boolean isServer) {
        this.isServer = isServer;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.name() + ": CustomPacketDecoderHandler.channelReadComplete");

        ctx.channel().flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(ctx.name() + ": CustomPacketDecoderHandler.channelRead");
        var auth = false;
        var logMsg = "";
        Object orid = msg;

        var dpk = (DatagramPacket) msg;

        var loginPkRep = new LoginResponsePacket();
        loginPkRep.setSuccess(true);
        var dpkRep = UdpMyPacketCodeC.INSTANCE.encode(ctx, loginPkRep, new InetSocketAddress("192.168.1.105", 9007));

        ctx.channel().write(dpkRep);


        return;
        /*
        if (dpk != null) {
            orid = dpk;

            if (dpk.content() != null) {
                orid = dpk.content();

                var packet = PacketCodeC.INSTANCE.decode(dpk.content());
                if (packet != null) {
                    orid = packet;

                    auth = true;
                    switch (packet.getCommand()) {
                        case ICommand.LOGIN_REQUEST:
                            var loginpk = (LoginRequestPacket) packet;
                            orid = loginpk;
                            auth = true;
                            System.out.println("登陆包"+loginpk.toString());
                            //回包
                            var loginPkRep = new LoginResponsePacket();
                            loginPkRep.setSuccess(true);
                            var dpkRep = UdpMyPacketCodeC.INSTANCE.encode(ctx, loginPkRep, dpk.sender());
                            ctx.writeAndFlush(dpkRep);
                            break;

                        case ICommand.LOGIN_RESPONSE:
                            var loginreppk = (LoginResponsePacket) packet;
                            if(loginreppk.isSuccess()){
                                System.out.println("登录成功.");
                            }else{
                                System.out.println("登陆失败.");
                            }
                            break;

                        default: auth = false; break;
                    }
                }
            }
        }


        if(auth){
            System.out.println("解包成功: "+orid.getClass().getSimpleName()+"{"+orid.toString()+"}");
        }else{
            System.out.println("解包失败: "+orid.getClass().getSimpleName()+"{"+orid.toString()+"}");
        }

         */
        //ctx.fireChannelReadComplete();
    }

}

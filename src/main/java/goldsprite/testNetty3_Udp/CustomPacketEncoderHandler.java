package goldsprite.testNetty3_Udp;


import goldsprite.testNetty.samples.packets.Packet;
import goldsprite.testNetty.samples.packets.PacketCodeC;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

public class CustomPacketEncoderHandler extends ChannelOutboundHandlerAdapter {
    private final boolean isServer;

    public CustomPacketEncoderHandler(boolean isServer) {
        this.isServer = isServer;
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.name()+": CustomPacketEncoderHandler.flush");
        ctx.flush();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println(ctx.name()+": CustomPacketEncoderHandler.write");
        ctx.write(msg);
//        ctx.writeAndFlush(msg);
//        Packet pk = (Packet) msg;
//        var byteBuf = PacketCodeC.INSTANCE.encode(ctx.alloc(), pk);
//
//        var dpk = new DatagramPacket(byteBuf, new InetSocketAddress("192.168.1.105", 8007));
//        ctx.channel().writeAndFlush(dpk);
    }


    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        var logMsg = "";
        Object orid = msg;

        var dpk = (DatagramPacket) msg;
        if (dpk != null) {
            orid = dpk;

            if (dpk.content() != null) {
                orid = dpk.content();

                ctx.fireChannelRead(dpk.content());
                return;
//                var packet = PacketCodeC.INSTANCE.decode(dpk.content());
//                if (packet != null) {
//                    orid = packet;
//
//                    switch (packet.getCommand()) {
//                        case ICommand.LOGIN_REQUEST:
//                            var login_request_pk = (LoginRequestPacket) packet;
//                            ctx.fireChannelRead(login_request_pk);
////                            ctx.fireChannelReadComplete();
//                            break;
//                    }
//                }
            }
        }
    }

}

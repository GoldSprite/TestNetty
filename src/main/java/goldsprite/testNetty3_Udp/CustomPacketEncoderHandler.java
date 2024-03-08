package goldsprite.testNetty3_Udp;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

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
}

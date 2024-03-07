package goldsprite.testNetty3_Udp;

import goldsprite.DateTools;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

class UdpChannelInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    boolean rep;

    UdpChannelInboundHandler(boolean rep) {
        this.rep = rep;
    }

    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {

        var buf = msg.content();
        String strMsg = buf.toString(CharsetUtil.UTF_8);
        System.out.println(DateTools.currentDateTime()+"recv: "+strMsg);


        if(rep) {
            ByteBuf buf1 = new UnpooledByteBufAllocator(true).buffer();
            buf1.writeCharSequence("ok", CharsetUtil.UTF_8);

            var packet = new DatagramPacket(buf1, msg.sender());

            ctx.writeAndFlush(packet).sync();
        }
    }
}


package goldsprite.testNetty3_Udp;

import goldsprite.DateTools;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import static goldsprite.LogTools.NLog;

public class UdpChannelInboundHandler extends ChannelInboundHandlerAdapter {

    boolean isResponse;
    boolean isServer;

    UdpChannelInboundHandler(boolean isResponse) {
        this.isServer = this.isResponse = isResponse;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        var buf = (ByteBuf)msg;
//        var buf = msg.content();
        String strMsg = buf.toString(CharsetUtil.UTF_8);
        NLog(UdpClient.localAddress2, "recv: "+strMsg);


        if(isResponse) {
            ByteBuf buf1 = new UnpooledByteBufAllocator(true).buffer();
            buf1.writeCharSequence("ok", CharsetUtil.UTF_8);

//            var packet = new DatagramPacket(buf1, msg.sender());

            ctx.writeAndFlush(buf1).sync();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NLog(UdpClient.remoteAddress2, "频道异常: "+cause);
        //ctx.channel().close();
    }
}


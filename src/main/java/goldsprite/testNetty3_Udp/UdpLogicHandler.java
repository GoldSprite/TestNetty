package goldsprite.testNetty3_Udp;

import goldsprite.testNetty.samples.packets.LoginRequestPacket;
import goldsprite.testNetty.samples.packets.LoginResponsePacket;
import goldsprite.testNetty3_Udp.codec.UdpMyPacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

import static goldsprite.LogTools.NLog;

public class UdpLogicHandler extends ChannelInboundHandlerAdapter {

    boolean isResponse;
    boolean isServer;

    UdpLogicHandler(boolean isResponse) {
        this.isServer = this.isResponse = isResponse;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.name() + ": UdpLogicHandler.channelReadComplete");
        ctx.fireChannelReadComplete();
    }

    //入栈
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        System.out.println(ctx.name() + ": UdpLogicHandler.channelRead");

        var dpk = (DatagramPacket)packet;
        var strMsg = UdpMyPacketCodeC.INSTANCE.decode(dpk).toString();
        NLog(UdpClient.localAddress2, "recv: "+strMsg);
//
//
//        if(isResponse) {
//            ByteBuf buf1 = new UnpooledByteBufAllocator(true).buffer();
//            buf1.writeCharSequence("ok", CharsetUtil.UTF_8);
//
//            var pkRep = new DatagramPacket(buf1, pk.sender());
//
//            ctx.writeAndFlush(pkRep).sync();
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NLog(UdpClient.remoteAddress2, "频道异常: " + cause);
        //ctx.channel().close();
    }
}


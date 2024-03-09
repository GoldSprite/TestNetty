package goldsprite.testNetty3_Udp;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class CustomPacketDecoderHandler extends ChannelInboundHandlerAdapter {
    private boolean isServer;
    private InetSocketAddress sender;

    public CustomPacketDecoderHandler(){}
    public CustomPacketDecoderHandler(boolean isServer) {
        this.isServer = isServer;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.name() + ": CustomPacketDecoderHandler.channelReadComplete");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(ctx.name() + ": CustomPacketDecoderHandler.channelRead");
        var channel = ctx.channel();

        if(msg instanceof DatagramPacket){
            ctx.fireChannelRead((DatagramPacket)msg);
        }else{
            throw new Exception("数据包格式异常.");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(getClass().getSimpleName()+"处理器异常: "+cause);
    }
}

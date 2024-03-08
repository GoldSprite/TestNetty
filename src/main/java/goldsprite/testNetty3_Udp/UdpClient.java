package goldsprite.testNetty3_Udp;

import goldsprite.DateTools;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Random;

import static goldsprite.LogTools.NLog;

public class UdpClient {
    Channel channel;

    public static void main(String[] args) {

        UdpClient client = new UdpClient();

        client.startClient();
    }


    /**
     * 点对点
     */
    public static final String addr1 = "10.0.0.2";  //
    public static final String addr1_w = "162.14.68.248";  //
    public static final String addr2 = "192.168.1.105";
    public static final String addr2_w = "112.195.244.151";
    public static InetSocketAddress remoteAddress = new InetSocketAddress(addr2, 8001);  //localhost, 8888
    public static InetSocketAddress remoteAddress2 = new InetSocketAddress(addr2_w, 34001);  //localhost, 8888
    public static InetSocketAddress localAddress = new InetSocketAddress(addr2, 30000);  //localhost, 8888
    public static InetSocketAddress localAddress2 = new InetSocketAddress(addr2_w, 30001);  //localhost, 8888

    /** 广播地址
     InetSocketAddress remoteAddress = new InetSocketAddress("255.255.255.255", 9000)
     */

    /** 组播地址
     InetSocketAddress remoteAddress = new InetSocketAddress("239.8.8.1", 9000)
     */

    void startClient() {

        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioDatagramChannel.class)
                .handler(new ChannelInitializer() {
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast("recv", new UdpChannelInboundHandler(false));
                    }
                });
        try {
            channel = (NioDatagramChannel) b.bind(localAddress).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //循环发消息
        var msg = "客户端消息xx"+
                "一二三四五六七八九十"
                ;
        sendMsg(channel, remoteAddress2, msg, 1, 16);  //3750
    }

    public static void sendMsg(Channel channel, InetSocketAddress address, final String msgf, int tick, int delayMillis) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < tick; i++) {
                    var msg = msgf;
//                    var msg = msgf + " "+i;
                    NLog(localAddress2, msg);

                    ByteBuf buf = new UnpooledByteBufAllocator(true).buffer();
                    buf.writeCharSequence(msg, CharsetUtil.UTF_8);
                    try {
                        var ra = new InetSocketAddress("192.168.1.105", 8007);
                        var packet = new DatagramPacket(buf, ra);
                        channel.writeAndFlush(packet).sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try{Thread.sleep(delayMillis);}catch(Exception e){}
                }

                NLog(localAddress2, "send finish..");
            }
        }).start();
    }

}


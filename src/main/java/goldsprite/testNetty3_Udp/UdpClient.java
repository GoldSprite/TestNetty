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
import java.util.Random;

class UdpClient {
    Channel channel;

    public static void main(String[] args) {

        UdpClient client = new UdpClient();

        client.startClient();
    }


    /**
     * 点对点
     */
    public static InetSocketAddress remoteAddress = new InetSocketAddress("10.0.0.2", 60000);  //localhost, 8888
    public static InetSocketAddress localAddress = new InetSocketAddress("10.0.0.2", 30000);  //localhost, 8888
    public static InetSocketAddress localAddress2 = new InetSocketAddress("10.0.0.2", 30001);  //localhost, 8888

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
        sendMsg(channel, remoteAddress, msg, 1250, 8);
    }

    public static void sendMsg(Channel channel, InetSocketAddress address, final String msgf, int tick, int delayMillis) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < tick; i++) {
                    var msg = msgf + " "+i;
                    System.out.println(DateTools.currentDateTime()+ address+ " send msg: "+msg);

                    ByteBuf buf = new UnpooledByteBufAllocator(true).buffer();
                    buf.writeCharSequence(msg, CharsetUtil.UTF_8);
                    var packet = new DatagramPacket(buf, address);
                    try {
                        channel.writeAndFlush(packet).sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try{Thread.sleep(delayMillis);}catch(Exception e){}
                }

                System.out.println( "send finish..");
            }
        }).start();
    }

}


package goldsprite.testNetty3_Udp;

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

class UdpClient {
    Channel channel;

    public static void main(String[] args) {

        UdpClient client = new UdpClient();

        client.startClient();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    var msg = "msg $i";
                    System.out.println(  "send msg: $msg");
                    client.sendMsg(msg);
                    try{Thread.sleep(1000);}catch(Exception e){}
                }

                System.out.println( "send finish..");
            }
        }).start();
    }


    /**
     * 点对点
     */
    InetSocketAddress remoteAddress = new InetSocketAddress("192.168.1.105", 60000);  //localhost, 8888
    InetSocketAddress localAddress = new InetSocketAddress("192.168.1.105", 1000);  //localhost, 8888

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
    }

    void sendMsg(String msg) {

        ByteBuf buf = new UnpooledByteBufAllocator(true).buffer();
        buf.writeCharSequence(msg, CharsetUtil.UTF_8);

        var packet = new DatagramPacket(buf, remoteAddress);

        try {
            channel.writeAndFlush(packet).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}


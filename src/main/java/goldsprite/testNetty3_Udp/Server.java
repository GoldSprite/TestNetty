package goldsprite.testNetty3_Udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.NetUtil;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

import static goldsprite.LogTools.NLog;

public class Server {
    public static Server Instance;

    public List<CustomPacketHandler> clients = new ArrayList<>();

    public int clientCount() {
        return clients.size();
    }


    public static void main(String[] args) {

        Instance = new Server();

        InetSocketAddress address = new InetSocketAddress("192.168.1.105", 60000);  //239.8.8.1, 51888

//        NLog(UdpClient.remoteAddress, "哈哈哈哈");
        Instance.run(address);
    }


    void run(InetSocketAddress groupAddress) {
        EventLoopGroup group = new NioEventLoopGroup();

        try {

            Bootstrap b = new Bootstrap();

            b
                    .group(group)
                    .channel(NioDatagramChannel.class)
//                    .channelFactory(new ChannelFactory<NioDatagramChannel>() {
//                        @Override
//                        public NioDatagramChannel newChannel() {
//                            return new NioDatagramChannel(InternetProtocolFamily.IPv4);
//                        }
//                    })
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast("1", new CustomPacketDecoderHandler(true));
                            ch.pipeline().addLast("2", new CustomPacketHandler(true));
                            ch.pipeline().addLast("3", new CustomPacketEncoderHandler(true));
                        }
                    });

            NioDatagramChannel ch = (NioDatagramChannel) b.bind(new InetSocketAddress("192.168.1.105", 8007)).sync().channel();


            NetworkInterface ni = NetUtil.LOOPBACK_IF;

            NLog(UdpClient.remoteAddress2, "$ni.name : $ni.displayName");

//            ch.joinGroup(groupAddress, ni).sync();

            NLog(UdpClient.remoteAddress2, "udp server($groupAddress.hostName:$groupAddress.port) is running...");


            //循环发消息
            var msg = "服务端消息xx" +
                    "一二三四五六七八九十";
//            UdpClient.sendMsg(ch, UdpClient.localAddress, msg, 3750, 16);
//            UdpClient.sendMsg(ch, UdpClient.localAddress2, msg, 3750, 16);


            ch.closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void addClient(CustomPacketHandler ctx) {
        if (clients.contains(ctx)) {
            clients.remove(ctx);
        }
        clients.add(ctx);
    }
}


package goldsprite.myUdpNetty.starter;

import goldsprite.myUdpNetty.codec.packets.LoginRequestPacket;
import goldsprite.myUdpNetty.handlers.PacketDecoder;
import goldsprite.myUdpNetty.handlers.PacketEncoder;
import goldsprite.myUdpNetty.handlers.PacketsHandler;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import goldsprite.myUdpNetty.other.ClientInfoStatus;
import goldsprite.myUdpNetty.tools.LogTools;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.stream.Collectors;

//import lombok.var;

import static goldsprite.myUdpNetty.tools.LogTools.NLog;

public class Server {
    public static Server Instance;
    public static InetSocketAddress localAddress = new InetSocketAddress("0.0.0.0", 34001);  //内
    public static InetSocketAddress networkAddress = new InetSocketAddress("192.168.1.105", 34001);  //本机局域外
//    public static InetSocketAddress networkAddress = new InetSocketAddress("112.195.244.107", 34001);  //本机网络外
//    public static InetSocketAddress networkAddress = new InetSocketAddress("162.14.68.248", 34001);  //云服外
    public static boolean enableHeartBeats = false;
    public static boolean strangerIntercept = false;
    public static int heartTicker = 1000 * 15 * 10;  //millis
    public static int heartInterval = 1000 * 1;  //millis

    public HashMap<Integer, ClientInfoStatus> clients = new HashMap<>();
    public int clientCount() {
        return clients.size();
    }
    public boolean isOnline(int playerGuid) {
        return clients.containsKey(playerGuid);
    }
    public int endGuid = 0;
    private Channel channel;


    public static void main(String[] args) {

        Instance = new Server();

        Instance.run();
    }


    void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap()
                    .group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast("1", new PacketDecoder(true));
                            ch.pipeline().addLast("2", new PacketsHandler(true));
                            ch.pipeline().addLast("3", new PacketEncoder(true));
                        }
                    });
            channel = (NioDatagramChannel) b.bind(localAddress).sync().channel();

            NLog("$ni.name : $ni.displayName");
            NLog("udp server($groupAddress.hostName:$groupAddress.port) is running...");

            if(enableHeartBeats)
                startHeartBeatThread();

            channel.closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    private void startHeartBeatThread() {
        new Thread(() -> {
            while (channel.isActive()) {
                var removeList = clients.entrySet().stream().filter(p -> {
//                    LogTools.NLog("心跳线程..");
                    var clientInfo = p.getValue();
                    //移除离线客户端
                    if (System.currentTimeMillis() > clientInfo.afkHearts) {
                        LogTools.NLog("客户端[" + p.getKey() + "-" + clientInfo.name + "-" + clientInfo.address + "]已离线.");
                        return true;
                    }
                    return false;
                }).map(p -> p.getKey()).collect(Collectors.toList());
                for (var id : removeList) clients.remove(id);

                try {
                    Thread.sleep(heartInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean loginClient(LoginRequestPacket pk, InetSocketAddress sender, int newGuid) {
        if (isOnline(pk.getOwnerGuid())) return false;

        var clientInfo = new ClientInfoStatus();
        clientInfo.address = sender;
        clientInfo.name = pk.getUserName();
        clientInfo.loginTimeMillis = System.currentTimeMillis();
        clientInfo.afkHearts = System.currentTimeMillis() + heartTicker;
        clients.put(newGuid, clientInfo);
        return true;
    }

    public void sendPacket(Packet pk) {
        channel.writeAndFlush(pk);
    }

}


package goldsprite.testNetty3_Udp;

import goldsprite.packets.MyPackets.HeartBeatRequestPacket;
import goldsprite.packets.MyPackets.LoginRequestPacket;
import goldsprite.packets.Packet;
import goldsprite.testNetty3_Udp.other.ClientInfoStatus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.stream.Collectors;

import static goldsprite.LogTools.NLog;

public class Server {
    public static Server Instance;
    public InetSocketAddress ra = new InetSocketAddress("192.168.1.105", 9007);
    public HashMap<Integer, ClientInfoStatus> clients = new HashMap<>();
    public int endGuid = 0;
    private Channel channel;

    public int clientCount() {
        return clients.size();
    }
    public static int heartTicker = 1000 * 15 * 1;  //millis


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
                            ch.pipeline().addLast("1", new CustomPacketDecoderHandler(true));
                            ch.pipeline().addLast("2", new CustomPacketHandler(true));
                            ch.pipeline().addLast("3", new CustomPacketEncoderHandler(true));
                        }
                    });
            channel = (NioDatagramChannel) b.bind(new InetSocketAddress("192.168.1.105", 8007)).sync().channel();

            NLog(UdpClient.remoteAddress2, "$ni.name : $ni.displayName");
            NLog(UdpClient.remoteAddress2, "udp server($groupAddress.hostName:$groupAddress.port) is running...");

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
                    System.out.println("心跳线程..");
                    var clientInfo = p.getValue();
                    //移除离线客户端
                    if (System.currentTimeMillis() > clientInfo.afkHearts) {
                        System.out.println("客户端[" + p.getKey() + "-" + clientInfo.name + "-" + clientInfo.address + "]已离线.");
                        return true;
                    }
                    return false;
                }).map(p -> p.getKey()).collect(Collectors.toList());
                for (var id : removeList) clients.remove(id);

                try {
                    Thread.sleep(900);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean addClient(LoginRequestPacket loginpk, InetSocketAddress sender, int newGuid) {
        if (clients.containsKey(loginpk.getOwnerGuid())) return false;
//        if (clients.containsKey(loginpk.getOwnerGuid())) {
//            clients.remove(loginpk.getOwnerGuid());
//        }

        var clientInfo = new ClientInfoStatus();
        clientInfo.address = sender;
        clientInfo.name = loginpk.getUserName();
        clientInfo.loginTimeMillis = System.currentTimeMillis();
        clientInfo.afkHearts = System.currentTimeMillis() + heartTicker;
        clients.put(newGuid, clientInfo);
        return true;
    }

    public void sendPacket(Packet pk) {
        channel.writeAndFlush(pk);
    }

}


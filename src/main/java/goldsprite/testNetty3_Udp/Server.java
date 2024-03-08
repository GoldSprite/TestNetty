package goldsprite.testNetty3_Udp;

import goldsprite.packets.MyPackets.LoginRequestPacket;
import goldsprite.packets.PacketCodeC;
import goldsprite.testNetty3_Udp.other.ClientInfoStatus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.NetUtil;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static goldsprite.LogTools.NLog;

public class Server {
    public static Server Instance;
    public HashMap<Integer, ClientInfoStatus> clients = new HashMap<>();
    public int endGuid = 0;

    public int clientCount() {
        return clients.size();
    }

    public static int heartTicker = 15;  //s


    public static void main(String[] args) {

        Instance = new Server();

        InetSocketAddress address = new InetSocketAddress("192.168.1.105", 60000);  //239.8.8.1, 51888

//        NLog(UdpClient.remoteAddress, "哈哈哈哈");
        Instance.run(address);
    }


    void run(InetSocketAddress groupAddress) {
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
            NioDatagramChannel ch = (NioDatagramChannel) b.bind(new InetSocketAddress("192.168.1.105", 8007)).sync().channel();

            NLog(UdpClient.remoteAddress2, "$ni.name : $ni.displayName");
            NLog(UdpClient.remoteAddress2, "udp server($groupAddress.hostName:$groupAddress.port) is running...");

            new Thread(() -> {
                while (ch.isActive()) {
                    var removeList = clients.entrySet().stream().filter(p -> {
                        System.out.println("心跳线程..");
                        var clientInfo = p.getValue();
                        //移除离线客户端
                        if (System.currentTimeMillis() / 1000 > clientInfo.afkHearts) {
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

            ch.closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
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
        clientInfo.afkHearts = System.currentTimeMillis() / 1000 + heartTicker;
        clients.put(newGuid, clientInfo);
        return true;
    }
}


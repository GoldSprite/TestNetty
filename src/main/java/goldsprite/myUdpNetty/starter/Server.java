package goldsprite.myUdpNetty.starter;

import goldsprite.myUdpNetty.codec.packets.LoginRequestPacket;
import goldsprite.myUdpNetty.handlers.PacketDecoder;
import goldsprite.myUdpNetty.handlers.PacketEncoder;
import goldsprite.myUdpNetty.handlers.PacketsHandler;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import goldsprite.myUdpNetty.other.ClientInfoStatus;
import goldsprite.myUdpNetty.tools.ILogLevel;
import goldsprite.myUdpNetty.tools.LogTools;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Collectors;

//import lombok.var;

import static goldsprite.myUdpNetty.tools.LogTools.NLogInfo;

public class Server {
    public static Server Instance;
    public static InetSocketAddress localAddress = new InetSocketAddress("0.0.0.0", 34001);  //内
    /*
    用于表示服务端地址以便客户端定位并发送packet到服务端
     */
    public static InetSocketAddress networkAddress = new InetSocketAddress("192.168.1.105", 34001);  //本机局域外
    //    public static InetSocketAddress networkAddress = new InetSocketAddress("192.168.1.105", 34001);  //本机局域外
//    public static InetSocketAddress networkAddress = new InetSocketAddress("112.195.244.107", 34001);  //本机网络外
//    public static InetSocketAddress networkAddress = new InetSocketAddress("162.14.68.248", 34001);  //云服外
    public static boolean enableHeartBeats = false;
    public static boolean strangerIntercept = true;//是否拦截陌生人
    public static int heartTicker = 1000 * 15 * 10;//心跳超时时间(毫秒)
    public static int heartInterval = 1000;//心跳检测间隔, 每n(毫秒)检测一次

    public HashMap<Integer, ClientInfoStatus> clients = new HashMap<>();

    public int clientCount() {
        return clients.size();
    }

    public boolean isOnline(int playerGuid) {
        return clients.containsKey(playerGuid);
    }

    public int endGuid = 0;
    private Channel channel;


    public class LaunchCFG {
        public InetSocketAddress launchLocalAddress;
        public InetSocketAddress launchNetworkAddress;
        public boolean enableHeartBeats;
    }

    static boolean firstLaunch = false;

    public static void main(String[] args) {

        Instance = new Server();

        readCFG();
        if (firstLaunch) return;

        Instance.run();
    }

    private static void readCFG() {
        // 假设配置文件名为 server.properties
        Properties properties = new Properties();
        File dir = new File(System.getProperty("user.dir"));
        LogTools.NLog(ILogLevel.DEBUG, "运行所在目录: " + dir.getAbsolutePath());
        File file = new File(dir, "server.properties");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 如果文件不存在，则从 resources 目录复制
            InputStream inputStream = Server.class.getClassLoader().getResourceAsStream("server.properties");
            if (inputStream != null) {
                try (OutputStream outputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    LogTools.NLog(ILogLevel.INFO, "从 resources 目录复制 server.properties 文件到运行目录");
                    LogTools.NLog(ILogLevel.INFO, "创建初始配置完成, 请重新启动.");
                    //if(System.readKey()) System.exit(0);
                    // 添加按键监听
                    new Thread(() -> {
                        try {
                            while (true) {
                                if (System.in.read() == '\n') { // 检查是否按下回车键
                                    System.out.println("Exiting server...");
                                    System.exit(0);
                                }
                                Thread.sleep(100); // 避免CPU占用过高
                            }
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    firstLaunch = true;
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                LogTools.NLog(ILogLevel.ERROR, "无法从 resources 目录找到 server.properties 文件");
                return;
            }
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
            LogTools.NLog(ILogLevel.INFO, "读取配置文件: " + properties.toString());
            // 读取配置文件中的属性
            String localAddress = properties.getProperty("localAddress");
            int localPort = Integer.parseInt(properties.getProperty("localPort"));
            boolean enableHeartBeats = Boolean.parseBoolean(properties.getProperty("enableHeartBeats"));
            int heartTicker = Integer.parseInt(properties.getProperty("heartTicker"));
            // 设置服务器配置
            Server.localAddress = new InetSocketAddress(localAddress, localPort);
            Server.enableHeartBeats = enableHeartBeats;
            Server.heartTicker = heartTicker;
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            NLogInfo("$ni.name : $ni.displayName");
            NLogInfo("udp server($groupAddress.hostName:$groupAddress.port) is running...");

            if (enableHeartBeats)
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
                        LogTools.NLogInfo("客户端[" + p.getKey() + "-" + clientInfo.name + "-" + clientInfo.address + "]已离线.");
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
        LogTools.NLogDebug("登录玩家地址: " + sender);
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


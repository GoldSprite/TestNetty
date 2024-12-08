package goldsprite.myUdpNetty.starter;

import goldsprite.myUdpNetty.codec.codecInterfaces.IStatus;
import goldsprite.myUdpNetty.codec.packets.*;
import goldsprite.myUdpNetty.handlers.PacketDecoder;
import goldsprite.myUdpNetty.handlers.PacketEncoder;
import goldsprite.myUdpNetty.handlers.PacketsHandler;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import goldsprite.myUdpNetty.other.ClientInfoStatus;
import goldsprite.myUdpNetty.other.PacketCallback2;
import goldsprite.myUdpNetty.tools.ILogLevel;
import goldsprite.myUdpNetty.tools.LogTools;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.Scanner;

//import lombok.var;

public class Client {
    public static Client Instance;
    //    public static InetSocketAddress localAddress = new InetSocketAddress("0.0.0.0", -1);  //本地
    public static InetSocketAddress localAddress;  // 随机地址
    static{
        localAddress = new InetSocketAddress("0.0.0.0", getRandomPort());
    }
    public static int ServerWaitOutTime = 1000 * 3;  //millis
    public ClientInfoStatus server = new ClientInfoStatus();
    public boolean heartReply = false;
    @Getter
    @Setter
    private int ownerGuid = -1;
    Channel channel;

    static boolean firstLaunch = false;
    public static void main(String[] args) {

        Instance = new Client();

        readCFG();
        if(firstLaunch) return;

        Instance.startClient();
    }

    private static void readCFG() {
        // 假设配置文件名为 client.properties
        Properties properties = new Properties();
        File dir = new File(System.getProperty("user.dir"));
        LogTools.NLog(ILogLevel.DEBUG, "运行所在目录: " + dir.getAbsolutePath());
        File file = new File(dir, "client.properties");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 如果文件不存在，则从 resources 目录复制
            InputStream inputStream = Client.class.getClassLoader().getResourceAsStream("client.properties");
            if (inputStream!= null) {
                try (OutputStream outputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    LogTools.NLog(ILogLevel.DEBUG, "从 resources 目录复制 client.properties 文件到运行目录");
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
                LogTools.NLog(ILogLevel.ERROR, "无法从 resources 目录找到 client.properties 文件");
                return;
            }
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
            LogTools.NLog(ILogLevel.DEBUG, "读取配置文件: " + properties.toString());
            // 读取配置文件中的属性
            String localAddress = properties.getProperty("localAddress");
            String localPortStr = properties.getProperty("localPort");
            int localPort = localPortStr!=null?Integer.parseInt(localPortStr):getRandomPort();
            String networkAddress = properties.getProperty("networkAddress");
            int networkPort = Integer.parseInt(properties.getProperty("networkPort"));
            boolean enableHeartBeats = Boolean.parseBoolean(properties.getProperty("enableHeartBeats"));
            // 设置服务器配置
            Client.localAddress = new InetSocketAddress(localAddress, localPort);
            Server.networkAddress = new InetSocketAddress(networkAddress, networkPort);
            Server.enableHeartBeats = enableHeartBeats;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点对点
     */
//    public static final String addr1 = "10.0.0.2";  //
//    public static final String addr1_w = "162.14.68.248";  //
//    public static final String addr2 = "192.168.1.105";
//    public static final String addr2_w = "112.195.244.151";
//    public static InetSocketAddress remoteAddress = new InetSocketAddress(addr2, 8001);  //localhost, 8888
//    public static InetSocketAddress remoteAddress2 = new InetSocketAddress(addr2_w, 34001);  //localhost, 8888
//    public static InetSocketAddress localAddress = new InetSocketAddress(addr2, 30000);  //localhost, 8888
//    public static InetSocketAddress localAddress2 = new InetSocketAddress(addr2_w, 30001);  //localhost, 8888

    /** 广播地址
     InetSocketAddress remoteAddress = new InetSocketAddress("255.255.255.255", 9000)
     */

    /**
     * 组播地址
     * InetSocketAddress remoteAddress = new InetSocketAddress("239.8.8.1", 9000)
     */

    void startClient() {
        bind();

        LogTools.NLogInfo("客户端启动成功.");
        LogTools.NLogInfo("请先登录以访问服务器.");
        LogTools.NLogInfo(helpMsg);
        while (true) {
            Scanner scan = new Scanner(System.in);
            var str = scan.nextLine();
            str = str.replaceFirst("/", "");
            var cmd = str.split(" ");
            try {
                DecodeCommand(cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bind() {
        try {
            NioEventLoopGroup group = new NioEventLoopGroup();

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer() {
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast("1", new PacketDecoder());
                            ch.pipeline().addLast("2", new PacketsHandler());
                            ch.pipeline().addLast("3", new PacketEncoder());
                        }
                    });
            channel = (NioDatagramChannel) b.bind(localAddress).sync().channel();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    String helpMsg = "please input [/help] to show the commands menu.";

    private void DecodeCommand(String[] cmd) throws Exception {
        var cmdHead = cmd[0];
        switch (cmdHead) {
            case "help": {
                var helpManual = "commands: "
                        + "\n登录: /login name password"
//                        + "\n在线玩家列表: /list"
//                        + "\n移动: /move x y z"
                        + "\n消息: /msg message..."
                        + "\n广播: /broadcast message..."
                        + "\n日志等级: /loglevel int-level(1~5 ERR WARN DEBUG INFO MSG) int-onoff(1~0)"
//                        + "\n自杀: /kill"
                        ;
                LogTools.NLog(ILogLevel.FORCE, helpManual);
                break;
            }
            case "msg": {
                var msg = String.join(" ", cmd);
                msg = msg.replaceFirst("msg ", "");
                LogTools.NLogMsg("你发了: " + msg);
                cmd_SendMsg(msg);
                break;
            }
            case "broadcast": {
                var msg = String.join(" ", cmd);
                msg = msg.replaceFirst("broadcast ", "");
                LogTools.NLogMsg("你广播了: " + msg);
                cmd_SendBroadcast(msg);
                break;
            }
            case "login": {
                cmd_Login(cmd[1], cmd[2]);
                break;
            }
            case "list": {
                break;
            }
            case "move": {
                break;
            }
            case "loglevel": {
                var key = Integer.parseInt(cmd[1]);
                var onoff = "1".equals(cmd[2]);
                LogTools.logLevels.replace(key, onoff);
                LogTools.NLog(ILogLevel.FORCE, "logLevel-" + ILogLevel.getLogMsg(key) + (onoff ? "开启" : "关闭") + ".");
                break;
            }
            default:
                LogTools.NLogInfo(helpMsg);
        }
    }

    private void cmd_Login(String userName, String password) {
        var pk = new LoginRequestPacket(getOwnerGuid(), userName, password);
        sendPacket(pk, LoginResponsePacket.class, (rep) -> {
            if (IStatus.isSuccessStatus(rep)) {
                LogTools.NLogMsg("登录成功.");
            }
        });
    }

    private void cmd_SendMsg(String msg) {
        var pk = new MessageRequestPacket(getOwnerGuid(), msg);
        sendPacket(pk, MessageResponsePacket.class, (rep) -> {
            if (IStatus.isSuccessStatus(rep))
                LogTools.NLogMsg("信息发送成功.");
        });
    }

    private void cmd_SendBroadcast(String msg) {
        var pk = new BroadcastRequestPacket(getOwnerGuid(), msg);
        sendPacket(pk, BroadcastResponsePacket.class, (rep) -> {
            if (IStatus.isSuccessStatus(rep)) {
                LogTools.NLogMsg("广播发送成功.");
            }
        });
    }

    public void sendPacket(Packet pk) {
        pk.setOwnerGuid(getOwnerGuid());
        channel.writeAndFlush(pk);
    }

    public <T extends Packet> void sendPacket(Packet pk, Class<T> ppid, PacketCallback2<T> callback) {
        var handler = (PacketsHandler) channel.pipeline().context("2").handler();
        handler.addCallbackListener(ppid, callback);
        sendPacket(pk);
    }

    private static int getRandomPort() {
        int randomPort;
        do {
            randomPort = (int) (Math.random() * 10000 + 30000);
        } while (isPortInUse(randomPort));
        return randomPort;
    }

    private static boolean isPortInUse(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

}


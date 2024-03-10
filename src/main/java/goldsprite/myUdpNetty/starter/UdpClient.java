package goldsprite.myUdpNetty.starter;

import goldsprite.myUdpNetty.codec.packets.HeartBeatRequestPacket;
import goldsprite.myUdpNetty.codec.packets.MoveResponsePacket;
import goldsprite.myUdpNetty.handlers.PacketDecoder;
import goldsprite.myUdpNetty.handlers.PacketEncoder;
import goldsprite.myUdpNetty.handlers.PacketsHandler;
import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;
import goldsprite.myUdpNetty.codec.packets.MoveRequestPacket;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import goldsprite.myUdpNetty.other.PacketCallback;
import goldsprite.myUdpNetty.other.ClientInfoStatus;
import goldsprite.myUdpNetty.other.PacketCallback2;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.UUID;

import lombok.var;

import static goldsprite.myUdpNetty.tools.LogTools.NLog;

public class UdpClient {
    public static UdpClient Instance;
    public static InetSocketAddress localAddress = new InetSocketAddress("0.0.0.0", 8007);  //本地
    public static int ServerWaitOutTime = 1000 * 3;  //millis
    public ClientInfoStatus server = new ClientInfoStatus();
    public boolean heartReply = false;
    public int ownerGuid = -1;
    Channel channel;

    public static void main(String[] args) {

        Instance = new UdpClient();

        Instance.startClient();
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

        System.out.println(helpMsg);
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
            case "help":
                var helpManual = "commands: "
                        + "\n登录: /login name password"
                        + "\n在线玩家列表: /list"
                        + "\n移动: /move x y z"
//                        + "\n自杀: /kill"
                        ;
                System.out.println(helpManual);
                break;
            case "login":
                var name = cmd[1];
                var pwd = cmd[2];
                var pk = new goldsprite.myUdpNetty.codec.packets.LoginRequestPacket(name, pwd);
                System.out.println("发登录包..");
                sendPacket(pk, (p) -> {
                    var str = "登陆包响应: ";
                    if (ICommand.RETURN_SUCCESS.equals(p.getCode())) {
                        str += "登录成功.";

                    } else {
                        str += "登录失败: " + p.getReason();
                    }
                    System.out.println(str);
                });
//                login(cmd[1], cmd[2]);
                break;
            case "list":
                queryRoomInfoAsync(new PacketCallback() {
                    @Override
                    public void callback(Packet pk) {
                        var qryreppk = (goldsprite.myUdpNetty.codec.packets.QueryRoomInfoResponsePacket) pk;
                        System.out.println("在线玩家数: " + qryreppk.getPlayerCount());
                        System.out.println("在线玩家列表: [" + String.join(", ", qryreppk.getPlayerList()) + "]");
                    }
                });
                break;
            case "move":
                Move(Float.parseFloat(cmd[1]), Float.parseFloat(cmd[2]), Float.parseFloat(cmd[3]));
                break;

            default:
                System.out.println(helpMsg);
        }
    }

    private void queryRoomInfoAsync(PacketCallback callback) {
//        var ppid = UUID.randomUUID().toString();
//        var qrypk = new QueryRoomInfoPacket(ppid);
//        var pkHandler = (CustomPacketHandler) channel.pipeline().context("2").handler();
//        try {
//            pkHandler.addCallbackListener(callback);
//            channel.writeAndFlush(dpk);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void login(String name, String pwd) {
//        var callback = new PacketCallback() {
//            public void run(Packet pk) {
//                //初始化sever信息
//                server.loginTimeMillis = System.currentTimeMillis();
//                server.afkHearts = server.loginTimeMillis + Server.heartTicker;
//            }
//        };
//        var pkHandler = (CustomPacketHandler) channel.pipeline().context("2").handler();
//        pkHandler.addCallbackListener(callback);
//        var loginPk = new LoginRequestPacket(name, pwd, callback.ppid);
//        sendPacket(loginPk);
    }

    private void Move(float x, float y, float z) {
        try {
            var mpk = new MoveRequestPacket();
            mpk.setPos(x, y, z);
            boolean[] reply = {false};
            new Thread(() -> {
                try {
                    sendPacket(mpk, (MoveResponsePacket pk) -> {
                        reply[0] = true;
                    });
                    Thread.sleep(500);
                    if(!reply[0]){
                        System.out.println("服务器无回复.");
                    }
                } catch (Exception e) {
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet pk) {
        pk.setOwnerGuid(ownerGuid);
        channel.writeAndFlush(pk);
    }

    public <T extends Packet> void sendPacket(Packet pk, PacketCallback2<T> callback) {
        var ppid = UUID.randomUUID().toString();
        var handler = (PacketsHandler) channel.pipeline().context("2").handler();
        handler.addCallbackListener(ppid, callback);
        pk.setPpid(ppid);
        sendPacket(pk);
    }

    public void startHeartBeatThread() {
        new Thread(() -> {
            while (true) {
                try {
                    //发心跳包
                    var hpk = new goldsprite.myUdpNetty.codec.packets.HeartBeatRequestPacket(System.currentTimeMillis());
                    heartReply = false;
                    sendPacket(hpk, (HeartBeatRequestPacket hreppk) -> {
                        //服务器回应
                        heartReply = true;
                        server.afkHearts = hreppk.getHeartMillis() + UdpServer.heartTicker;
                    });
                    Thread.sleep(UdpClient.ServerWaitOutTime);
                    if (!heartReply) {  //没收到回应
                        System.out.println("心跳" + UdpClient.ServerWaitOutTime / 1000 + "s无回应, 服务器已离线.");
                        return;
                    }

                    Thread.sleep(UdpServer.heartInterval);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }
}


package goldsprite.testNetty3_Udp;

import goldsprite.packets.MyPackets.LoginRequestPacket;
import goldsprite.packets.MyPackets.MoveRequestPacket;
import goldsprite.packets.MyPackets.QueryRoomInfoPacket;
import goldsprite.packets.MyPackets.QueryRoomInfoResponsePacket;
import goldsprite.packets.Packet;
import goldsprite.packets.PacketCodeC;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import static goldsprite.LogTools.NLog;

public class UdpClient {
    public static UdpClient Instance;

    Channel channel;
    InetSocketAddress ra2 = new InetSocketAddress("192.168.1.105", 8007);


    public static void main(String[] args) {

        Instance = new UdpClient();

        Instance.startClient();
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

    /**
     * 组播地址
     * InetSocketAddress remoteAddress = new InetSocketAddress("239.8.8.1", 9000)
     */

    void startClient() {
        bind();

        System.out.println(helpMsg);
        while (true) {
            Scanner scan = new Scanner(System.in);
            var str = scan.nextLine().replaceFirst("/", "");
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
                            ch.pipeline().addLast("1", new CustomPacketDecoderHandler(true));
                            ch.pipeline().addLast("2", new CustomPacketHandler(true));
                            ch.pipeline().addLast("3", new CustomPacketEncoderHandler(true));
                        }
                    });
            channel = (NioDatagramChannel) b.bind(new InetSocketAddress("192.168.1.105", 9007)).sync().channel();

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
                login(cmd[1], cmd[2]);
                break;
            case "list":
                queryRoomInfoAsync(new PacketCallback() {
                    @Override
                    public void callback(Packet pk) {
                        var qryreppk = (QueryRoomInfoResponsePacket)pk;
                        System.out.println("在线玩家数: " + qryreppk.getPlayerCount());
                        System.out.println("在线玩家列表: " + String.join(", ", qryreppk.getPlayerList()));
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
        var qrypk = new QueryRoomInfoPacket(callback.ppid);
        var dpk = PacketCodeC.INSTANCE.encodeDpk(channel.alloc(), qrypk, ra2);
        var pkHandler = (CustomPacketHandler) channel.pipeline().context("2").handler();
        try {
            pkHandler.addCallbackListener(callback);
            channel.writeAndFlush(dpk).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login(String name, String pwd) {
        try {
            var loginPk = new LoginRequestPacket(name, pwd);
            var dpk = PacketCodeC.INSTANCE.encodeDpk(channel.alloc(), loginPk, ra2);

            channel.writeAndFlush(dpk).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Move(float x, float y, float z) {

        try {
            var ra = new InetSocketAddress("192.168.1.105", 9007);
            var ra2 = new InetSocketAddress("192.168.1.105", 8007);
            var mpk = new MoveRequestPacket();
            mpk.setPos(x, y, z);
            var dpk = PacketCodeC.INSTANCE.encodeDpk(channel.alloc(), mpk, ra2);

            channel.writeAndFlush(dpk).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMsg(Channel channel, InetSocketAddress address, final String msgf, int tick, int delayMillis) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < tick; i++) {
                    var msg = msgf;
//                    var msg = msgf + " "+i;
                    NLog(localAddress2, "登录");

                    try {
                        var ra = new InetSocketAddress("192.168.1.105", 9007);
                        var ra2 = new InetSocketAddress("192.168.1.105", 8007);
                        var loginPk = new LoginRequestPacket(
                                new Random().nextInt(10000) + "",
                                "Admin"
                        );
                        var dpk = PacketCodeC.INSTANCE.encodeDpk(channel.alloc(), loginPk, ra2);

                        channel.writeAndFlush(dpk).sync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(delayMillis);
                    } catch (Exception e) {
                    }
                }

                NLog(localAddress2, "send finish..");
            }
        }).start();
    }


    public class PacketCallback{
        public String ppid;

        public PacketCallback(){
            ppid = UUID.randomUUID().toString();
        }
        public void callback(Packet pk){}
    }


}


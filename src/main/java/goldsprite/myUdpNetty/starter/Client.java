package goldsprite.myUdpNetty.starter;

import goldsprite.myUdpNetty.codec.codecInterfaces.IStatus;
import goldsprite.myUdpNetty.codec.packets.*;
import goldsprite.myUdpNetty.handlers.PacketDecoder;
import goldsprite.myUdpNetty.handlers.PacketEncoder;
import goldsprite.myUdpNetty.handlers.PacketsHandler;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import goldsprite.myUdpNetty.other.ClientInfoStatus;
import goldsprite.myUdpNetty.other.PacketCallback2;
import goldsprite.myUdpNetty.tools.LogTools;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.Scanner;

//import lombok.var;

public class Client {
    public static Client Instance;
    public static InetSocketAddress localAddress = new InetSocketAddress("0.0.0.0", 8007);  //本地
    public static int ServerWaitOutTime = 1000 * 3;  //millis
    public ClientInfoStatus server = new ClientInfoStatus();
    public boolean heartReply = false;
    @Getter
    @Setter
    private int ownerGuid = -1;
    Channel channel;

    public static void main(String[] args) {

        Instance = new Client();

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

        LogTools.NLog(helpMsg);
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
//                        + "\n登录: /login name password"
//                        + "\n在线玩家列表: /list"
//                        + "\n移动: /move x y z"
                        + "\n消息: /msg message..."
//                        + "\n自杀: /kill"
                        ;
                LogTools.NLog(helpManual);
                break;
            }
            case "broadcast": {
                var msg = String.join(" ", cmd);
                msg = msg.replaceFirst("msg ", "");
                LogTools.NLog("你发了: "+msg);
                cmd_SendMsg(msg);
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
            default:
                LogTools.NLog(helpMsg);
        }
    }

    private void cmd_Login(String userName, String password) {
        var pk = new LoginRequestPacket(getOwnerGuid(), userName, password);
        sendPacket(pk, LoginResponsePacket.class, (rep) -> {
            if (IStatus.isSuccessStatus(rep)){
                setOwnerGuid(rep.getOwnerGuid());
                LogTools.NLog("登录成功.");
            }
        });
    }

    private void cmd_SendMsg(String msg) {
        var pk = new MessageRequestPacket(getOwnerGuid(), msg);
        sendPacket(pk, MessageResponsePacket.class, (rep) -> {
            if(IStatus.isSuccessStatus(rep))
                LogTools.NLog("广播发送成功.");
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

}


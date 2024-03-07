package goldsprite.testNetty;

import goldsprite.DateTools;
import goldsprite.testNetty.samples.NettyServer;
import goldsprite.testNetty.samples.packets.MyPackets.MessageRequestPacket;
import goldsprite.testNetty.samples.packets.PacketCodeC;
import io.netty.channel.ChannelHandlerContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class TestNetty {
    public static final String SOCKET_IP = "192.168.1.105";
    public static final int UDP_SOCKET_PORT = 8000;
    public static final int TCP_SOCKET_PORT = 8001;

    private static ServerSocket server = null;


    public static void main(String[] args) throws Exception {

        //启动tcp服务
        var tcpServer = new NettyServer();
        InetSocketAddress tcpAddress = new InetSocketAddress(SOCKET_IP, TCP_SOCKET_PORT);
        tcpServer.start(tcpAddress);
    }


    public static void testLoopMes(ChannelHandlerContext ctx, final int repeat) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                var delay = 1000;
                try {
                    var pk = new MessageRequestPacket();
                    for (int i=repeat; i > 0; i--) {
                        pk.setMessage("123456789");
//                pk.setMessage("哈哈哈哈"+new Random().nextInt(1000));
                        var requestBuf = PacketCodeC.INSTANCE.encode(ctx.alloc(), pk);
                        ctx.channel().writeAndFlush(requestBuf);

                        System.out.println(DateTools.currentDateTime() + (delay / 1000f) + "s后发送消息.");
                        Thread.sleep(delay);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(DateTools.currentDateTime() + "消息已全部发送.");
            }
        }).start();
    }
}

package goldsprite.testNetty;

import goldsprite.testNetty.samples.NettyServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

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
}

package goldsprite;

import java.net.InetSocketAddress;
import goldsprite.testNetty3_Udp.UdpClient;

public class LogTools {
    public static void NLog(InetSocketAddress sender, String msg){
        var isServer = sender.getHostString().equals(UdpClient.remoteAddress2.getHostString()) && sender.getPort()==UdpClient.remoteAddress2.getPort();
        System.out.println(
                DateTools.currentDateTime()+
                        "["+(isServer?"SERVER":"CLIENT")+"] "+
                        "["+sender.getHostString()+":"+sender.getPort()+"] "+
                        msg
        );
    }
}

package goldsprite.myUdpNetty.tools;

import java.net.InetSocketAddress;
import goldsprite.myUdpNetty.starter.UdpClient;

public class LogTools {
//    public static void NLog(InetSocketAddress sender, String msg){
//        boolean isServer = sender.getHostString().equals(UdpClient.remoteAddress2.getHostString()) && sender.getPort()==UdpClient.remoteAddress2.getPort();
//        LogTools.NLog(
//                DateTools.currentDateTime()+
//                        "["+(isServer?"SERVER":"CLIENT")+"] "+
//                        "["+sender.getHostString()+":"+sender.getPort()+"] "+
//                        msg
//        );
//    }

    public static void NLog(Object msg){
        LogTools.NLog(
                DateTools.currentDateTime()+
                        msg.toString()
        );
    }
}

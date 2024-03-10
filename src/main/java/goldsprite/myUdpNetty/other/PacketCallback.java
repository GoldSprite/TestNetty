package goldsprite.myUdpNetty.other;


import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;

import java.util.UUID;

public class PacketCallback{
    public String ppid;

    public PacketCallback(){
        ppid = UUID.randomUUID().toString();
    }
    public void callback(Packet pk){}
}
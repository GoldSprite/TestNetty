package goldsprite.testNetty3_Udp.other;


import goldsprite.packets.Packet;

import java.util.UUID;

public class PacketCallback{
    public String ppid;

    public PacketCallback(){
        ppid = UUID.randomUUID().toString();
    }
    public void callback(Packet pk){}
}
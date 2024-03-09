package goldsprite.testNetty3_Udp.other;


import goldsprite.packets.Packet;

import java.util.UUID;

public interface PacketCallback2<T extends Packet> {

    public void callback(T pk);
}
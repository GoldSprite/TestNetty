package goldsprite.myUdpNetty.other;


import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;

public interface PacketCallback2<T extends Packet> {

    public void callback(T pk);
}
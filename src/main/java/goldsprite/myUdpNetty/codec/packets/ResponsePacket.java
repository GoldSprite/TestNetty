package goldsprite.myUdpNetty.codec.packets;

import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;

public abstract class ResponsePacket extends Packet {
    public ResponsePacket(int ownerGuid, int repCode) {
        super(ownerGuid);
        setCode(repCode);
    }
}

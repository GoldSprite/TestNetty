package goldsprite.myUdpNetty.codec.packets;

import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import lombok.Getter;

public class BroadcastResponsePacket extends ResponsePacket {
    @Getter
    private String message;

    public BroadcastResponsePacket(int ownerGuid, int repCode, String message) {
        super(ownerGuid, repCode);
        this.message = message;
    }

    @Override
    public byte getCommand() {
        return ICommand.BROADCAST_RESPONSE;
    }
}

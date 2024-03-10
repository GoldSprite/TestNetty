package goldsprite.myUdpNetty.codec.packets;

import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import lombok.Getter;

//通知就行, 不用管到不到位
public class BroadcastRequestPacket extends Packet {
    @Getter
    private String message;

    public BroadcastRequestPacket(int ownerGuid, String message) {
        super(ownerGuid);
        this.message = message;
    }

    @Override
    public byte getCommand() {
        return ICommand.BROADCAST_REQUEST;
    }
}

package goldsprite.myUdpNetty.codec.packets;

import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import lombok.*;

public class MessageRequestPacket extends Packet {
    @Getter
    private String message;

    public MessageRequestPacket(int ownerGuid, String message) {
        super(ownerGuid);
        this.message = message;
    }

    @Override
    public byte getCommand() {
        return ICommand.MESSAGE_REQUEST;
    }
}

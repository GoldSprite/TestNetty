package goldsprite.testNetty.samples.packets.MyPackets;

import static goldsprite.testNetty.samples.packets.ICommand.MESSAGE_REQUEST;
import goldsprite.testNetty.samples.packets.Packet;
import lombok.Data;

@Data
public class MessageRequestPacket extends Packet {
    private String message;

    @Override
    public byte getCommand(){
        return MESSAGE_REQUEST;
    }
}

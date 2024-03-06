package goldsprite.testNetty.samples.packets.MyPackets;

import goldsprite.testNetty.samples.packets.Packet;
import lombok.Data;

import static goldsprite.testNetty.samples.packets.ICommand.MESSAGE_RESPONSE;

@Data
public class MessageResponsePacket extends Packet {
    private boolean success;
    private String reason;

    @Override
    public byte getCommand(){
        return MESSAGE_RESPONSE;
    }
}

package goldsprite.packets.MyPackets;

import goldsprite.packets.ICommand;
import goldsprite.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MessageRequestPacket extends Packet {
    private String message;

    @Override
    public byte getCommand(){
        return ICommand.MESSAGE_REQUEST;
    }
}

package goldsprite.packets.MyPackets;

import goldsprite.packets.ICommand;
import goldsprite.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MoveResponsePacket extends Packet {
    private boolean success;
    private String reason;

    @Override
    public byte getCommand() {
        return ICommand.MOVE_RESPONSE;
    }
}
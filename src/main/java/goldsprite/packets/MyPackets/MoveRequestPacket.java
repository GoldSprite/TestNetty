package goldsprite.packets.MyPackets;

import goldsprite.packets.ICommand;
import goldsprite.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MoveRequestPacket extends Packet {
    private float[] pos = new float[3];

    public void setPos(float x, float y, float z) {
        pos[0] = x;
        pos[1] = y;
        pos[2] = z;
    }

    @Override
    public byte getCommand() {
        return ICommand.MOVE_REQUEST;
    }
}

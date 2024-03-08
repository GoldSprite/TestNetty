package goldsprite.packets.MyPackets;

import goldsprite.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import static goldsprite.packets.ICommand.QUERYROOMINFO_REQUEST;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class QueryRoomInfoPacket extends Packet {
    private String ppid;
    @Override
    public byte getCommand() {
        return QUERYROOMINFO_REQUEST;
    }
}

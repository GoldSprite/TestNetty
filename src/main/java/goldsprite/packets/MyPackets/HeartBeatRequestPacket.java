package goldsprite.packets.MyPackets;

import goldsprite.packets.ICommand;
import goldsprite.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HeartBeatRequestPacket extends Packet {
    private long heartMillis;
    private String ppid;
    @Override
    public byte getCommand() {
        return ICommand.HEARTBEAT_REQUEST;
    }
}

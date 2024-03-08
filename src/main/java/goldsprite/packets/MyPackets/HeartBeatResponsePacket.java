package goldsprite.packets.MyPackets;

import goldsprite.packets.ICommand;
import goldsprite.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HeartBeatResponsePacket extends Packet {
    private boolean success;
    private String reason;
    private String ppid;
    private long heartMillis;

    @Override
    public byte getCommand() {
        return ICommand.HEARTBEAT_RESPONSE;
    }
}
package goldsprite.myUdpNetty.codec.packets;

import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HeartBeatRequestPacket extends Packet {
    private long heartMillis;

    @Override
    public byte getCommand() {
        return ICommand.HEARTBEAT_REQUEST;
    }
}

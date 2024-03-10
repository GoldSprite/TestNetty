package goldsprite.myUdpNetty.codec.packets;

import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class QueryRoomInfoRequestPacket extends Packet {
    private String ppid;
    @Override
    public byte getCommand() {
        return ICommand.QUERYROOMINFO_REQUEST;
    }
}

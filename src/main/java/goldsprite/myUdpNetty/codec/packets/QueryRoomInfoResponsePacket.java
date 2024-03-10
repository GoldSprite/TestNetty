package goldsprite.myUdpNetty.codec.packets;

import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class QueryRoomInfoResponsePacket extends Packet {
    private boolean success;
    private String reason;
    private int playerCount;
    private int maxPlayerCount;
    private String[] playerList;

    @Override
    public byte getCommand() {
        return ICommand.QUERYROOMINFO_RESPONSE;
    }
}
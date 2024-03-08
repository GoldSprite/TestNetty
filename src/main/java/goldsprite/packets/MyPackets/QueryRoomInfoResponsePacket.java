package goldsprite.packets.MyPackets;

import goldsprite.packets.ICommand;
import goldsprite.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static goldsprite.packets.ICommand.QUERYROOMINFO_REQUEST;
import static goldsprite.packets.ICommand.QUERYROOMINFO_RESPONSE;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class QueryRoomInfoResponsePacket extends Packet {
    private boolean success;
    private String reason;
    private String ppid;
    private int playerCount;
    private int maxPlayerCount;
    private String[] playerList;

    @Override
    public byte getCommand() {
        return ICommand.QUERYROOMINFO_RESPONSE;
    }
}
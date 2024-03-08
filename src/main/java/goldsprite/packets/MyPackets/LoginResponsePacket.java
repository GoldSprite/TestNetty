package goldsprite.packets.MyPackets;

import goldsprite.packets.ICommand;
import goldsprite.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponsePacket extends Packet {
    private boolean success;
    private String reason;

    @Override
    public byte getCommand() {
        return ICommand.LOGIN_RESPONSE;
    }
}

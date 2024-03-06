package goldsprite.testNetty.samples.packets;

import lombok.Data;

@Data
public class LoginResponsePacket extends Packet {
    private boolean success;
    private String reason;

    @Override
    public byte getCommand() {
        return ICommand.LOGIN_RESPONSE;
    }
}

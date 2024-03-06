package goldsprite.testNetty.samples.packets;

import lombok.Data;

@Data
public class LoginRequestPacket extends Packet {
    private int userId;
    private String userName;
    private String password;

    @Override
    public byte getCommand() {
        return ICommand.LOGIN_REQUEST;
    }
}

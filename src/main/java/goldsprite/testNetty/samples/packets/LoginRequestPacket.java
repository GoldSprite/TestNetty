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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LoginRequestPacket");
        sb.append('{');
        sb.append("userId='").append(userId).append('\'');
        sb.append(", userName=").append(userName);
        sb.append(", password=").append(password);
        sb.append('}');
        return sb.toString();
    }

}

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LoginRequestPacket");
        sb.append('{');
        sb.append("success='").append(success).append('\'');
        sb.append(", reason=").append(reason);
        sb.append('}');
        return sb.toString();
    }
}

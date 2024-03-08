package goldsprite.packets.MyPackets;

import goldsprite.packets.ICommand;
import goldsprite.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequestPacket extends Packet {
    protected String userName;
    protected String password;

    @Override
    public byte getCommand() {
        return ICommand.LOGIN_REQUEST;
    }

//    @Override
//    public String toString() {
//        final StringBuilder sb = new StringBuilder("LoginRequestPacket");
//        sb.append('{');
//        sb.append(", userName=").append(userName);
//        sb.append(", password=").append(password);
//        sb.append('}');
//        return sb.toString();
//    }

}

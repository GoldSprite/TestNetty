package goldsprite.myUdpNetty.codec.packets;

import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
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

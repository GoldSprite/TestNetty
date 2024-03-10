package goldsprite.myUdpNetty.codec.packets;

import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import lombok.Getter;

public class LoginRequestPacket extends Packet {
    @Getter
    private String userName;
    @Getter
    private String password;

    public LoginRequestPacket(int ownerGuid, String userName, String password) {
        super(ownerGuid);
        this.userName = userName;
        this.password = password;
    }

    @Override
    public byte getCommand() {
        return ICommand.LOGIN_REQUEST;
    }

}

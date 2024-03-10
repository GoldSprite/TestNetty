package goldsprite.myUdpNetty.codec.packets;


import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;

public class LoginResponsePacket extends ResponsePacket {

    public LoginResponsePacket(int ownerGuid, int repCode) {
        super(ownerGuid, repCode);
    }

    @Override
    public byte getCommand() {
        return ICommand.LOGIN_RESPONSE;
    }
}

package goldsprite.myUdpNetty.codec.packets;

import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;

public class MessageResponsePacket extends ResponsePacket {

    public MessageResponsePacket(int ownerGuid, int repCode) {
        super(ownerGuid, repCode);
    }

    @Override
    public byte getCommand() {
        return ICommand.MESSAGE_RESPONSE;
    }
}
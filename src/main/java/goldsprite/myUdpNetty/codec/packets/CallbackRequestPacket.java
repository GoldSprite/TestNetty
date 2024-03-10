package goldsprite.myUdpNetty.codec.packets;

import goldsprite.myUdpNetty.codec.codecInterfaces.ICommand;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import lombok.Data;

@Data
public class CallbackRequestPacket extends Packet {
    private String ppid="";

    @Override
    public byte getCommand() {
        return ICommand.CALLBACK_REQUEST;
    }
}

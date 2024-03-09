package goldsprite.packets;

import lombok.Data;

@Data
public class CallbackRequestPacket extends Packet{
    private String ppid="";

    @Override
    public byte getCommand() {
        return ICommand.CALLBACK_REQUEST;
    }
}

package goldsprite.myUdpNetty.codec.codecInterfaces;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public abstract class Packet {
    private final byte version = 1;
    private int ownerGuid = -1;
    private int code = IStatus.SEND_SUCCESS;

    public Packet(int ownerGuid){
        setOwnerGuid(ownerGuid);
    }

    public abstract byte getCommand();
}

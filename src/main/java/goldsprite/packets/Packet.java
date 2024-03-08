package goldsprite.packets;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

public abstract class Packet {
    @Getter
    protected byte version = 1;
    @Getter @Setter
    private int ownerGuid = -1;

//    public Packet(){}
//    public Packet(InetSocketAddress sender, InetSocketAddress recipient){
//        this.sender = sender;
//        this.recipient = recipient;
//    }

    public abstract byte getCommand();
}

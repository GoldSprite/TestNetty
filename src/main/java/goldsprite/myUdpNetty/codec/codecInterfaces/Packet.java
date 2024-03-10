package goldsprite.myUdpNetty.codec.codecInterfaces;


import lombok.Getter;
import lombok.Setter;

public abstract class Packet {
    @Getter
    protected byte version = 1;
    @Getter @Setter
    private int ownerGuid = -1;
    @Getter @Setter
    private String code = ICommand.SEND_REQUEST;
    @Getter @Setter
    protected String reason = "";

//    public Packet(){}
//    public Packet(InetSocketAddress sender, InetSocketAddress recipient){
//        this.sender = sender;
//        this.recipient = recipient;
//    }

    public abstract byte getCommand();
}

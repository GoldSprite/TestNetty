package goldsprite.testNetty.samples.packets;


import lombok.Data;

@Data
public abstract class Packet {
    private byte version = 1;

    public abstract byte getCommand();
}

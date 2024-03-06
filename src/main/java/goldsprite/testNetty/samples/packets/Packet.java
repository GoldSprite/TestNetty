package goldsprite.testNetty.samples.packets;


import lombok.Data;

@Data
public abstract class Packet {
    private byte version = 1;

    abstract byte getCommand();
}

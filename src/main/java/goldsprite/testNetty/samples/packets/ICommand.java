package goldsprite.testNetty.samples.packets;

/**
 * 增加指令时务必在{@link PacketCodeC#PacketCodeC()}增加引用.
 */
public interface ICommand {
    byte LOGIN_REQUEST= 1;
    byte LOGIN_RESPONSE= 2;

    byte MESSAGE_REQUEST= 3;
    byte MESSAGE_RESPONSE= 4;
}

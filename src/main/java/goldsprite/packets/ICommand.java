package goldsprite.packets;

/**
 * 增加指令时务必在{@link PacketCodeC#PacketCodeC()}增加引用.
 */
public interface ICommand {
    byte LOGIN_REQUEST= 1;
    byte LOGIN_RESPONSE= 2;

    byte MESSAGE_REQUEST= 3;
    byte MESSAGE_RESPONSE= 4;

    byte MOVE_REQUEST= 5;
    byte MOVE_RESPONSE= 6;

    byte QUERYROOMINFO_REQUEST= 7;
    byte QUERYROOMINFO_RESPONSE= 8;

    byte HEARTBEAT_REQUEST = 9;
    byte HEARTBEAT_RESPONSE = 10;
}

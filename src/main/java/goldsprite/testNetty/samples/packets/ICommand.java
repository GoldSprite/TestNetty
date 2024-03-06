package goldsprite.testNetty.samples.packets;

public interface ICommand {
    byte LOGIN_REQUEST= 1;
    byte LOGIN_RESPONSE= 2;

    byte MESSAGE_REQUEST= 3;
    byte MESSAGE_RESPONSE= 4;
}

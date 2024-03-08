package goldsprite.packets;

public interface ISerializer {
    ISerializer DEFAULT = new JSONSerializer();

    byte getSerializerAlgorithm();
    byte[] serialize(Object object);
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}

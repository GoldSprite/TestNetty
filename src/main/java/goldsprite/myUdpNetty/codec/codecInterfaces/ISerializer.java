package goldsprite.myUdpNetty.codec.codecInterfaces;

import goldsprite.myUdpNetty.codec.serializers.JSONSerializer;

public interface ISerializer {
    ISerializer DEFAULT = new JSONSerializer();

    byte getSerializerAlgorithm();
    byte[] serialize(Object object);
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}

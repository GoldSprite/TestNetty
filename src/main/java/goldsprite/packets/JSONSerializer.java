package goldsprite.packets;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

public class JSONSerializer implements ISerializer {
    private Gson gson = new Gson();

    @Override
    public byte getSerializerAlgorithm() {
        return ISerializerAlgorithm.JSON;
    }

    @Override
    public byte[] serialize(Object object) {
        var json = gson.toJson(object);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        var str = new String(bytes, StandardCharsets.UTF_8);
        return gson.fromJson(str, clazz);
    }
}

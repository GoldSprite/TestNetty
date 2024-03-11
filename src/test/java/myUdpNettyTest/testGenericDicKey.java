package myUdpNettyTest;

import goldsprite.myUdpNetty.tools.LogTools;
//import lombok.var;

import java.util.HashMap;

/**
 * 测试获取泛型方法T实际类型: 结果失败
 */
public class testGenericDicKey {
    private HashMap<Class<? extends Packet2>, PacketCallback3> callbacks = new HashMap<>();

    public static void main(String[] args) {
        new testGenericDicKey();
    }

    public testGenericDicKey(){
        LogTools.NLogInfo(new APacket().getName());
        LogTools.NLogInfo(new BPacket().getName());

        //添加回调: 自动根据泛型指定字典键
        addCallback((BPacket pk)->{
            LogTools.NLogInfo(pk.getName()+": 客户端得到响应, 触发回调.");
        });

        var pkResponse = new BPacket();
        LogTools.NLogInfo("服务端创建响应包: "+pkResponse.getName());
        callbacks.get(pkResponse.getClass()).callback(new BPacket());
    }

    public <T extends Packet2> void addCallback(PacketCallback3<T> callback){
        //预计情况
        var clazz = BPacket.class;
        callbacks.put(clazz, callback);
//        //实际需要处理
//        var clazz = ?;
//        callbacks.put(clazz, callback);
    }
}

abstract  class Packet2{
    String getName(){ return getClass().getSimpleName(); }
}
class APacket extends Packet2{}
class BPacket extends Packet2{}

interface PacketCallback3<T extends Packet2>{
    void callback(T pk);
}

package goldsprite.testNetty3_Udp.other;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.concurrent.ConcurrentHashMap;

public class UserChannel {

    //用户id=>channel示例
    private final ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();


    /**
     * 判断一个通道是否有用户在使用
     * 可做信息转发时判断该通道是否合法
     * @param channel
     * @return
     */
    public boolean hasUser(Channel channel) {
        AttributeKey<String> key = AttributeKey.valueOf("user");channel.attr(key).remove();
        return (channel.hasAttr(key) || channel.attr(key).get() != null);//netty移除了这个map的remove方法,这里的判断谨慎一点
    }

    /**
     * 上线一个用户
     *
     * @param channel
     * @param userId
     */
    public void online(Channel channel, String userId) {
        //先判断用户是否在web系统中登录?
        //这部分代码个人实现,参考上面redis中的验证

        this.channelMap.put(userId, channel);
        AttributeKey<String> key = AttributeKey.valueOf("user");
        channel.attr(key).set(userId);


    }

    /**
     * 根据用户id获取该用户的通道
     *
     * @param userId
     * @return
     */
    public Channel getChannelByUserId(String userId) {
        return this.channelMap.get(userId);
    }

    /**
     * 判断一个用户是否在线
     *
     * @param userId
     * @return
     */
    public Boolean online(String userId) {
        return this.channelMap.containsKey(userId) && this.channelMap.get(userId) != null;
    }
}

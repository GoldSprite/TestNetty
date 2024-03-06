package goldsprite.testNetty.other;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

public class TestUdpServer {
    private final Bootstrap bootstrap;

    private final NioEventLoopGroup group;

    private Channel channel;

    public TestUdpServer(){
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.SO_RCVBUF, 1024 * 1024 * 100)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new NettyUdpServerHandler());
                    }
                });
    }

    public void start(InetSocketAddress address){
        try {
            bootstrap.bind(address).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

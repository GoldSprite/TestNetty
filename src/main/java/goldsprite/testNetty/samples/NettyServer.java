package goldsprite.testNetty.samples;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;

public class NettyServer {
    private final ServerBootstrap bootstrap;

    private final NioEventLoopGroup bossGroup;
    private final NioEventLoopGroup workerGroup;

    private Channel channel;

    public NettyServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024 * 512)
                .handler(new ChannelInitializer<NioServerSocketChannel>() {
                    protected void initChannel(NioServerSocketChannel ch) {
                        System.out.println("服务端启动中");
                    }
                })
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4));  //指定网络协议编码过滤器
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });
    }

    public void start(InetSocketAddress address) {
        try {
            System.out.println("Server start...");
            ChannelFuture cf = bootstrap.bind(address).sync();
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
//            断开连接
//            e.printStackTrace();
            System.out.println("客户端强制断开连接.");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

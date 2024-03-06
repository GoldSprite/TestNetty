package goldsprite.testNetty.samples;

import goldsprite.testNetty.samples.packets.LoginRequestPacket;
import goldsprite.testNetty.samples.packets.LoginResponsePacket;
import goldsprite.testNetty.samples.packets.MyPackets.MessageRequestPacket;
import goldsprite.testNetty.samples.packets.MyPackets.MessageResponsePacket;
import goldsprite.testNetty.samples.packets.Packet;
import goldsprite.testNetty.samples.packets.PacketCodeC;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final Bootstrap bootstrap;

    public NettyClientHandler(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(new Date() + ": 客户端开始登录");
        // 创建登录对象
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        loginRequestPacket.setUserId(new Random().nextInt(10000));
        loginRequestPacket.setUserName("username");
        loginRequestPacket.setPassword("pwd");
        // 编码
        ByteBuf buffer = PacketCodeC.INSTANCE.encode(ctx.alloc(), loginRequestPacket);
        // 写数据
        ctx.channel().writeAndFlush(buffer);
    }

    //接收服务端信息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        Packet packet = PacketCodeC.INSTANCE.decode(byteBuf);
        //如果数据类型是登录，就进行登录判断
        //同理可以判断数据是否是普通消息，还是其他类型的数据
        if (packet instanceof LoginResponsePacket) {
            LoginResponsePacket loginResponsePacket = (LoginResponsePacket) packet;
            if (loginResponsePacket.isSuccess()) {
                System.out.println(new Date() + ": 客户端登录成功");
                testLoopMes(ctx ,10);
            } else {
                System.out.println(new Date() + ": 客户端登录失败，原因：" + loginResponsePacket.getReason());
            }
        }else if(packet instanceof MessageResponsePacket){
            MessageResponsePacket pk = (MessageResponsePacket) packet;
            if(pk.isSuccess()){
                System.out.println("服务端验证: 消息发送成功");
            }else{
                System.out.println("服务端验证: 消息发送失败");
            }
        }
    }

    private void testLoopMes(ChannelHandlerContext ctx, int repeat) {
        var delay = 3;
        if(repeat>0){
            bootstrap.config().group().schedule(()->{
                var pk = new MessageRequestPacket();
                pk.setMessage("哈哈哈哈"+new Random().nextInt(1000));
                var requestBuf = PacketCodeC.INSTANCE.encode(ctx.alloc(), pk);
                ctx.channel().writeAndFlush(requestBuf);
                testLoopMes(ctx, repeat-1);
            }, delay, TimeUnit.SECONDS);
            System.out.println(delay+"s后发送消息.");
        }else{
            System.out.println("消息已全部发送.");
        }
    }
}
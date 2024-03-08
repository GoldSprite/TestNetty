package goldsprite.testNetty.samples;

import goldsprite.DateTools;
import goldsprite.testNetty.TestNetty;
import goldsprite.packets.MyPackets.LoginRequestPacket;
import goldsprite.packets.MyPackets.LoginResponsePacket;
import goldsprite.packets.MyPackets.MessageRequestPacket;
import goldsprite.packets.MyPackets.MessageResponsePacket;
import goldsprite.packets.Packet;
import goldsprite.packets.PacketCodeC;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final Bootstrap bootstrap;

    public NettyClientHandler(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("频道已激活.");

        System.out.println(new Date() + ": 开始登录");
        // 创建登录对象
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        loginRequestPacket.setUserName("username");
        loginRequestPacket.setPassword("pwd");
        // 编码
        ByteBuf buffer = PacketCodeC.INSTANCE.encode(ctx.alloc(), loginRequestPacket);
        // 写数据
        ctx.channel().writeAndFlush(buffer);


        //循环发包测试
        TestNetty.testLoopMes(ctx, 1);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("频道已结束.");
    }

    //接收服务端信息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(DateTools.currentDateTime() +"接收到数据包..."+byteBuf.toString(Charset.forName("utf-8")));

        Packet packet = PacketCodeC.INSTANCE.decode(byteBuf);
        //如果数据类型是登录，就进行登录判断
        //同理可以判断数据是否是普通消息，还是其他类型的数据
        if (packet instanceof LoginResponsePacket) {
            LoginResponsePacket loginResponsePacket = (LoginResponsePacket) packet;
            if (loginResponsePacket.isSuccess()) {
                System.out.println(new Date().toString() + ": 登录成功");
                Scanner scanner = new Scanner(System.in);
                var str = scanner.nextLine();
                System.out.println("你输入了: "+str);
                sendMessage(ctx, str);
//                testLoopMes(ctx ,300);
            } else {
                System.out.println(new Date() + ": 登录失败，原因：" + loginResponsePacket.getReason());
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

    private void sendMessage(ChannelHandlerContext ctx, String str) {
        var pk = new MessageRequestPacket();
        pk.setMessage(str);
        var requestBuf = PacketCodeC.INSTANCE.encode(ctx.alloc(), pk);
        ctx.channel().writeAndFlush(requestBuf);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("服务端出现异常: "+cause);
        ctx.close();
    }

}
package goldsprite.testNetty.samples;

import goldsprite.DateTools;
import goldsprite.testNetty.TestNetty;
import goldsprite.testNetty.samples.packets.LoginRequestPacket;
import goldsprite.testNetty.samples.packets.LoginResponsePacket;
import goldsprite.testNetty.samples.packets.MyPackets.MessageRequestPacket;
import goldsprite.testNetty.samples.packets.MyPackets.MessageResponsePacket;
import goldsprite.testNetty.samples.packets.Packet;
import goldsprite.testNetty.samples.packets.PacketCodeC;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final ServerBootstrap bootstrap;

    public NettyServerHandler(ServerBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[客户端"+ctx.channel().remoteAddress()+"]频道已激活.");


        //循环发包测试
        TestNetty.testLoopMes(ctx, 10);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[客户端"+ctx.channel().remoteAddress()+"]频道已下线.");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf requestByteBuf = (ByteBuf) msg;
        System.out.println(DateTools.currentDateTime() +"接收到数据包..."+requestByteBuf.toString(Charset.forName("utf-8")));
        // 解码
        Packet packet = null;
        try{
            System.out.println("解析到数据包...");
            packet = PacketCodeC.INSTANCE.decode(requestByteBuf);
            System.out.println("数据包解析完成: "+packet.getClass().getSimpleName());
        }catch(Exception e){
            System.out.println("Packet解包异常: 错误的未知格式数据.");
        }
        if(packet != null){
            // 判断是否是登录请求数据包
            if (packet instanceof LoginRequestPacket) {
                LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;
                LoginResponsePacket loginResponsePacket=new LoginResponsePacket();
                loginResponsePacket.setVersion(packet.getVersion());
                // 登录校验
                if (valid(loginRequestPacket)) {
                    // 校验成功
                    loginResponsePacket.setSuccess(true);
                    System.out.println("客户端登录验证成功！");
                } else {
                    // 校验失败
                    loginResponsePacket.setReason("账号或密码错误");
                    loginResponsePacket.setSuccess(false);
                    System.out.println("客户端登录失败！");
                }
                // 编码，结果发送给客户端
                ByteBuf responseByteBuf = PacketCodeC.INSTANCE.encode(ctx.alloc(), loginResponsePacket);
                ctx.channel().writeAndFlush(responseByteBuf);
            }else if(packet instanceof MessageRequestPacket){
                //处理
                MessageRequestPacket pk = (MessageRequestPacket) packet;
                System.out.println("接收: MessageRequestPacket: "+pk.getMessage());
                //回包
                MessageResponsePacket pk2 = new MessageResponsePacket();
                pk2.setSuccess(true);
                //编码发回
                ByteBuf responseBuf = PacketCodeC.INSTANCE.encode(ctx.alloc(), pk2);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ctx.channel().writeAndFlush(responseBuf);
                    }
                }).start();
            }
        }
    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        //这里可以查询数据库，验证用户的账号密码是否正确
        return true;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("客户端发生异常: "+cause);
        ctx.close();
    }
}

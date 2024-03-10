package goldsprite.myUdpNetty.handlers;


import goldsprite.myUdpNetty.codec.PacketCodeC;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import goldsprite.myUdpNetty.starter.UdpClient;
import goldsprite.myUdpNetty.starter.UdpServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

import lombok.var;

public class PacketEncoder extends ChannelOutboundHandlerAdapter {
    private boolean isServer;

    public PacketEncoder() {
        this.isServer = false;
    }

    public PacketEncoder(boolean isServer) {
        this.isServer = isServer;
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
//        System.out.println(ctx.name()+": CustomPacketEncoderHandler.flush");
        ctx.flush();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        System.out.println(ctx.name()+": CustomPacketEncoderHandler.write");

        //此处填充地址, 并将Packet包裹为DatagramPacket
        var pk = (Packet) msg;
        DatagramPacket dpk = null;
        InetSocketAddress networkAddress = null;
        if (!isServer) {
            //客户端往服务端: 定向地址, guid自动填充
            networkAddress = UdpServer.networkAddress;
        } else {
            //服务端往客户端: 客户端不定(包里自填guid), 地址附带

            var client = UdpServer.Instance.clients.get(pk.getOwnerGuid());
            networkAddress = client.address;
        }
        dpk = PacketCodeC.INSTANCE.encodeDpk(ctx.alloc(), pk, networkAddress);
        ctx.write(dpk);
//        ctx.writeAndFlush(msg);
//        Packet pk = (Packet) msg;
//        var byteBuf = PacketCodeC.INSTANCE.encode(ctx.alloc(), pk);
//
//        var dpk = new DatagramPacket(byteBuf, new InetSocketAddress("192.168.1.105", 8007));
//        ctx.channel().writeAndFlush(dpk);
    }
}

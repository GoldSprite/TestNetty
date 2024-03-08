package goldsprite.testNetty3_Udp.codec;

import goldsprite.testNetty.samples.packets.ISerializer;
import goldsprite.testNetty.samples.packets.LoginResponsePacket;
import goldsprite.testNetty.samples.packets.Packet;
import goldsprite.testNetty.samples.packets.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

public class UdpMyPacketCodeC {
    public static UdpMyPacketCodeC INSTANCE = new UdpMyPacketCodeC();

    public DatagramPacket encode(ChannelHandlerContext ctx, LoginResponsePacket loginPkRep, InetSocketAddress sender) {
        ByteBuf buf = PacketCodeC.INSTANCE.encode(ctx.alloc(), loginPkRep);
        var dpk = new DatagramPacket(buf, sender);
        return dpk;
    }

    public Packet decode(DatagramPacket dpk) {
        ByteBuf buf = dpk.content();
        var pk = PacketCodeC.INSTANCE.decode(buf);
        return pk;
    }
}

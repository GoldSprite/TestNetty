package goldsprite.myUdpNetty.handlers;


import goldsprite.myUdpNetty.codec.PacketCodeC;
import goldsprite.myUdpNetty.codec.codecInterfaces.Packet;
import goldsprite.myUdpNetty.other.ClientInfoStatus;
import goldsprite.myUdpNetty.starter.UdpServer;
import goldsprite.myUdpNetty.tools.LogTools;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

//import lombok.var;

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
//        LogTools.NLog(ctx.name()+": CustomPacketEncoderHandler.flush");
        ctx.flush();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        LogTools.NLog(ctx.name()+": CustomPacketEncoderHandler.write");
        DatagramPacket dpk = encodeDpk((Packet) msg, ctx);
        if(dpk != null)
            ctx.write(dpk);
    }

    //将Packet包裹为DatagramPacket
    private DatagramPacket encodeDpk(Packet pk, ChannelHandlerContext ctx) {
        InetSocketAddress networkAddress = UdpServer.networkAddress;
        if(isServer){
            ClientInfoStatus client = UdpServer.Instance.clients.get(pk.getOwnerGuid());
            networkAddress = client.address;
        }
        DatagramPacket dpk = PacketCodeC.INSTANCE.encodeDpk(ctx.alloc(), pk, networkAddress);
        if(completeAuthentication(pk, networkAddress)) return dpk;
        return null;
    }

    private boolean completeAuthentication(Packet pk, InetSocketAddress remoteAddress) {
        var info = "发包验证异常: ";
        if(remoteAddress == null){
            LogTools.NLog(info+"发送地址为空.");
            return false;
        }
        return true;
    }

}

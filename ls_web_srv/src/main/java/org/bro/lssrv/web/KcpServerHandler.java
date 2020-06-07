package org.bro.lssrv.web;

import io.jpower.kcp.netty.UkcpChannel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class KcpServerHandler extends AbsChannelAdapter {

    @Value("${kcp.conv}")
    private int conv;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Connect from {}", ctx.channel().remoteAddress());
        UkcpChannel kcpCh = (UkcpChannel) ctx.channel();
        kcpCh.conv(conv);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
//        short curCount = buf.getShort(buf.readerIndex());
        ctx.writeAndFlush(msg);

//        if (curCount == -1) {
//            ctx.close();
//        }
//        ByteBuf data = Unpooled.buffer(100);
//        data.writeByte(2);
//        ctx.fireChannelRead(data);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

}

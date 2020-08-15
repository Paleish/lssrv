package org.bro.lssrv.web;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.bro.lssrv.game.IPlayerManager;
import org.bro.lssrv.pb.SrvResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 功能描述: <br>
 * 〈发送消息类〉
 *
 * @return:
 * @since: 1.0.0
 * @Author:
 * @Date:
 */
@Service
@Slf4j
public class MessageSender {
    @Resource
    private IPlayerManager playerManager;

    //根据userId向玩家推送消息
    public void sendMsgByUserId(int userId, SrvResponse.SrvRes message) {
        //根据userId获取ctx
        Channel channel = playerManager.getCtxById(userId).channel();
        if (null == channel || !channel.isActive()) {
            //log.info("根据玩家userId-{}发送消息时，其网络已经断开", userId, Thread.getAllStackTraces());
        } else {
            sendSuccessMsg(channel, message);
        }
    }

    public void sendMsg(ChannelHandlerContext ctx, SrvResponse.SrvRes message) {
        if (null == ctx || !ctx.channel().isActive()) {
            //log.info("根据玩家userId-{}发送消息时，其网络已经断开", userId, Thread.getAllStackTraces());
        } else {
            sendSuccessMsg(ctx.channel(), message);
        }
    }

    private void sendSuccessMsg(Channel channel, SrvResponse.SrvRes sr) {
        try {
            ByteBuf b = UnpooledByteBufAllocator.DEFAULT.buffer();
            // 写入消息
            b.writeBytes(sr.toByteArray());
            WebSocketFrame frame = new BinaryWebSocketFrame(b);

            if (channel.isActive()) {
                if (channel.isWritable()) {
                    // 发送
                    channel.writeAndFlush(frame).addListener((ChannelFuture future) -> {
                        //消息发送成功
                        if (!future.isSuccess()) {
                            //消息发送失败
                            log.error("消息方法{}发送失败！用户地址为{},具体错误原因{}.", sr.getMethodId(), channel.remoteAddress(), future.cause().getMessage());
                        }
                    });
                } else {
                    try {
                        channel.writeAndFlush(frame).sync();
                        log.info("异步发送消息方法{}，发送成功!用户ip为{}", sr.getMethodId(), channel.remoteAddress());
                    } catch (InterruptedException e) {
                        log.error("write and flush msg exception. ", e);
                    }
                }
            } else {
                ReferenceCountUtil.release(b);
                //logger.error("send message{} channel{} is already close!", msgType, channel.remoteAddress());
            }
        } catch (Exception e) {
            log.error("消息{}发送失败", sr.getMethodId(), e);
            e.printStackTrace();
        }
    }
}

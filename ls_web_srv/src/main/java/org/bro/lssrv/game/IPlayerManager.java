package org.bro.lssrv.game;

import io.netty.channel.ChannelHandlerContext;

public interface IPlayerManager {

    void setPlayerId(ChannelHandlerContext ctx);

    int getPlayerId(ChannelHandlerContext ctx);

    ChannelHandlerContext getCtxById(int userId);

    int getRoomId(ChannelHandlerContext ctx);

    void setRoomId(ChannelHandlerContext ctx, int roomId);
}

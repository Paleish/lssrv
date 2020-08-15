package org.bro.lssrv.game.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.bro.lssrv.game.IPlayerManager;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class PlayerManager implements IPlayerManager {
    private String PLAYER_ID = "playerId";
    private String ROOM_ID = "roomId";
    public AttributeKey<Integer> playerIdKey = AttributeKey.valueOf(PLAYER_ID);
    public AttributeKey<Integer> roomIdKey = AttributeKey.valueOf(ROOM_ID);
    private Map<Integer, ChannelHandlerContext> map = new HashMap<>();

    @Override
    public void setPlayerId(ChannelHandlerContext ctx) {
        /**
         * 随机分配playerId
         */
        Random rd = new Random();
        int playerId = (rd.nextInt(9) + 1) * 10000 + rd.nextInt(9999);
        ctx.attr(playerIdKey).set(playerId);
        map.put(playerId, ctx);
    }

    @Override
    public int getPlayerId(ChannelHandlerContext ctx) {
        return ctx.attr(playerIdKey).get();
    }

    @Override
    public ChannelHandlerContext getCtxById(int userId) {
        return map.get(userId);
    }

    @Override
    public int getRoomId(ChannelHandlerContext ctx) {
        return ctx.attr(roomIdKey).get();
    }

    @Override
    public void setRoomId(ChannelHandlerContext ctx, int roomId) {
        ctx.attr(roomIdKey).set(roomId);
    }
}

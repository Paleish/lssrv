package org.bro.lssrv.web.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.bro.lssrv.game.IPlayerManager;
import org.bro.lssrv.game.IRoomManager;
import org.bro.lssrv.game.model.GameRoom;
import org.bro.lssrv.game.model.Player;
import org.bro.lssrv.pb.CliRequst;
import org.bro.lssrv.pb.GameProto;
import org.bro.lssrv.pb.SrvResponse;
import org.bro.lssrv.web.MessageSender;
import org.bro.lssrv.web.handler.IRequestHandler;
import org.springframework.stereotype.Service;
import schedule.ScheduledThreadPoolUtil;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class RequestHandler implements IRequestHandler {
    @Resource
    private IRoomManager roomManager;
    @Resource
    private IPlayerManager playerManager;
    @Resource
    private MessageSender messageSender;

    @Override
    public SrvResponse.SrvRes handleRequest(ChannelHandlerContext ctx, CliRequst.CliReq req) {

        switch (req.getMethodId()) {
            case cliEnterRoom:
                enterRoom(ctx, req);
                break;
            case cliInitOver:
                initOver(ctx);
                break;
            case cliOperate:
                recordOperate(ctx, req);
                break;
            default:
                break;
        }
        return null;
    }

    private void recordOperate(ChannelHandlerContext ctx, CliRequst.CliReq req) {
        int playerId = playerManager.getPlayerId(ctx);
        int roomId = playerManager.getRoomId(ctx);
        GameRoom room = roomManager.getRoomById(roomId);
        room.getOperateMap().put(playerId, req.getCliOperate());
    }

    private void initOver(ChannelHandlerContext ctx) {
        int playerId = playerManager.getPlayerId(ctx);
        int roomId = playerManager.getRoomId(ctx);
        GameRoom room = roomManager.getRoomById(roomId);
        Player player = room.getPlayerMap().get(playerId);
        player.setInitOver(true);
        //返回ack
        SrvResponse.SrvRes.Builder sr = SrvResponse.SrvRes.newBuilder();
        sr.setMethodId(SrvResponse.SrvMsgType.srvInitOver);
        messageSender.sendMsgByUserId(playerId, sr.build());
        //判断客户端是否都初始化完成
        boolean isInitOver = true;
        for (Player temp : room.getPlayerMap().values()) {
            if (!temp.isInitOver()) {
                isInitOver = false;
            }
        }
        if (isInitOver) {
            SrvResponse.SrvRes.Builder sr1 = SrvResponse.SrvRes.newBuilder();
            sr1.setMethodId(SrvResponse.SrvMsgType.bGameStart);
            room.broadcast(sr1.build());
            //房间开启定时任务
            ScheduledThreadPoolUtil.getInstance().addSchedule(room.getRoomId(), room, 0, 50, TimeUnit.MILLISECONDS);
        }
    }

    private void enterRoom(ChannelHandlerContext ctx, CliRequst.CliReq req) {
        int roomId = req.getCliEnterRoom().getRoomId();
        String nickName = req.getCliEnterRoom().getName();
        GameRoom gameRoom = roomManager.getRoomById(roomId);
        //将玩家加入房间
        int playerId = playerManager.getPlayerId(ctx);
        gameRoom.addPlayer(playerId, nickName);
        //绑定房间号和ctx的关系
        playerManager.setRoomId(ctx, gameRoom.getRoomId());
        //构建返回信息
        GameProto.SrvEnterRoom.Builder enterBuilder = GameProto.SrvEnterRoom.newBuilder();
        enterBuilder.setPlayerId(playerId);
        SrvResponse.SrvRes.Builder srvBuilder = SrvResponse.SrvRes.newBuilder();
        srvBuilder.setSrvEnterRoom(enterBuilder);
        srvBuilder.setMethodId(SrvResponse.SrvMsgType.srvEnterRoom);
        messageSender.sendMsg(ctx, srvBuilder.build());
        //判断房间人数
        if (gameRoom.getPlayerMap().size() == gameRoom.getPlayerNum()) {
            //推送游戏初始化数据
            GameProto.BGameInit.Builder initBuilder = GameProto.BGameInit.newBuilder();
            int randomSeed = (int) (System.currentTimeMillis() / 1000000);
            gameRoom.setRandomSeed(randomSeed);
            initBuilder.setSeed(randomSeed);
            //玩家信息
            int index = 0;
            for (Player player : gameRoom.getPlayerMap().values()) {
                GameProto.PlayerInfo.Builder playerBuilder = GameProto.PlayerInfo.newBuilder();
                playerBuilder.setName(player.getNickName());
                playerBuilder.setPlayerId(player.getPlayerId());
                playerBuilder.setStartPoint(index);
                index++;
                initBuilder.addPList(playerBuilder);
            }
            //广播
            SrvResponse.SrvRes.Builder sr = SrvResponse.SrvRes.newBuilder();
            sr.setBGameInit(initBuilder);
            sr.setMethodId(SrvResponse.SrvMsgType.bGameInit);
            gameRoom.broadcast(sr.build());
        }
    }
}

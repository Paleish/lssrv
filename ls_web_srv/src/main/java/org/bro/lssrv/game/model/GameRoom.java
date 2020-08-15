package org.bro.lssrv.game.model;

import lombok.Data;
import org.bro.lssrv.pb.GameProto;
import org.bro.lssrv.pb.SrvResponse;
import org.bro.lssrv.web.MessageSender;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Data
public class GameRoom implements Runnable {
    @Resource
    private MessageSender messageSender;

    private int roomId;//房间号
    private int playerNum = 2;//游戏开始人数
    private long randomSeed;//初始化种子
    private AtomicInteger frame = new AtomicInteger(1);//记录游戏帧数
    private Map<Integer, GameProto.CliOperate> operateMap = new HashMap<>();

    private Map<Integer, Player> playerMap = new HashMap<>();

    public GameRoom(int roomId) {
        this.roomId = roomId;
    }

    public void addPlayer(int playerId, String nickName) {
        Player player = new Player();
        player.setPlayerId(playerId);
        player.setNickName(nickName);

        playerMap.put(playerId, player);
    }

    public void broadcast(SrvResponse.SrvRes msg) {
        for (Player player : playerMap.values()) {
            messageSender.sendMsgByUserId(player.getPlayerId(), msg);
        }
    }

    @Override
    public void run() {
        GameProto.BGameFrame.Builder builder = GameProto.BGameFrame.newBuilder();
        builder.setFId(frame.getAndIncrement());
        synchronized (operateMap) {
            if (operateMap.size() > 0) {
                for (GameProto.CliOperate cliOperate : operateMap.values()) {
                    builder.addOperList(cliOperate);
                }
                operateMap.clear();
            }
        }
        SrvResponse.SrvRes.Builder sr = SrvResponse.SrvRes.newBuilder();
        sr.setMethodId(SrvResponse.SrvMsgType.bGameFrame);
        broadcast(sr.build());
    }
}

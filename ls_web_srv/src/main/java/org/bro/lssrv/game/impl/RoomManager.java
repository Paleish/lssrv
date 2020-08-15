package org.bro.lssrv.game.impl;

import org.bro.lssrv.game.IRoomManager;
import org.bro.lssrv.game.model.GameRoom;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RoomManager implements IRoomManager {

    private Map<Integer, GameRoom> roomMap = new HashMap<>();

    @Override
    public GameRoom getRoomById(int roomId) {
        if (roomMap.containsKey(roomId)) {
            return roomMap.get(roomId);
        } else {
            GameRoom gameRoom = new GameRoom(roomId);
            roomMap.put(roomId, gameRoom);
            return gameRoom;
        }
    }
}

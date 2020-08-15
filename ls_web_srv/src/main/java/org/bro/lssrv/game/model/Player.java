package org.bro.lssrv.game.model;

import lombok.Data;

@Data
public class Player {

    private int playerId;
    private String nickName;
    private boolean initOver = false;
}

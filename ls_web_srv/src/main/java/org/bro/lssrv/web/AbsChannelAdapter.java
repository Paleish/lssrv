package org.bro.lssrv.web;

import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbsChannelAdapter extends ChannelInboundHandlerAdapter {
    public static Logger logger;

    public AbsChannelAdapter() {
        this.logger = LogManager.getLogger(getClass().getName());
    }
}

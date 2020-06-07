package org.bro.lssrv.web;


import io.netty.bootstrap.UkcpServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
public class KcpServer {

    public static Logger logger = LogManager.getLogger(KcpServer.class.getName());

    @Resource(name = "kcpServerBootStrap")
    private UkcpServerBootstrap b;

    @Resource(name = "kcpPort")
    private Integer kcpPort;

    private ChannelFuture f;

    @Async
    public void start() throws Exception {
        f = b.bind(kcpPort).sync();
    }

    @PreDestroy
    public void stop() throws Exception {
        f.channel().closeFuture().sync().addListener(ChannelFutureListener.CLOSE);
    }
}

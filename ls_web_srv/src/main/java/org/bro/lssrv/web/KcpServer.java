package org.bro.lssrv.web;


import io.netty.bootstrap.UkcpServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
public class KcpServer {

    @Resource(name = "kcpServerBootStrap")
    private UkcpServerBootstrap b;

    @Resource(name = "kcpPort")
    private Integer kcpPort;

    private ChannelFuture f;

    @Async
    public void start() throws Exception {
        System.out.println(Thread.currentThread().getName());
        f = b.bind(kcpPort).sync();
        System.out.println(2);
    }

    @PreDestroy
    public void stop() throws Exception {
        f.channel().closeFuture().sync().addListener(ChannelFutureListener.CLOSE);
    }
}

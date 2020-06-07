package org.bro.lssrv.web;

import io.jpower.kcp.netty.ChannelOptionHelper;
import io.jpower.kcp.netty.UkcpChannel;
import io.jpower.kcp.netty.UkcpChannelOption;
import io.jpower.kcp.netty.UkcpServerChannel;
import io.netty.bootstrap.UkcpServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class KcpConfig {

    @Value("${kcp.bosscount}")
    private int bossCount;
    @Value("${kcp.port}")
    private int port;
    @Value("${kcp.conv}")
    private int conv;

    @Resource
    private KcpServerHandler kcpServerHandler;
    @Resource
    private TestHandler testHandler;

    @Bean(name = "kcpServerBootStrap")
    public UkcpServerBootstrap ukcpServerBootstrap() {
        // Configure the server.
        EventLoopGroup group = new NioEventLoopGroup();

        UkcpServerBootstrap b = new UkcpServerBootstrap();
        b.group(group)
                .channel(UkcpServerChannel.class)
                .childHandler(new ChannelInitializer<UkcpChannel>() {
                    @Override
                    public void initChannel(UkcpChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(kcpServerHandler);
                        p.addLast(testHandler);
                    }
                });
        ChannelOptionHelper.nodelay(b, true, 20, 2, true)
                .childOption(UkcpChannelOption.UKCP_MTU, 512);
        return b;
    }

    @Bean(name = "kcpPort")
    public Integer kcpPort() {
        return port;
    }
}

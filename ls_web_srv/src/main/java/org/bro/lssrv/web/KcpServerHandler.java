package org.bro.lssrv.web;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class KcpServerHandler extends ChannelInboundHandlerAdapter {

}

package org.bro.lssrv.web.handler;

import io.netty.channel.ChannelHandlerContext;
import org.bro.lssrv.pb.CliRequst;
import org.bro.lssrv.pb.SrvResponse;

public interface IRequestHandler {

    SrvResponse.SrvRes handleRequest(ChannelHandlerContext ctx, CliRequst.CliReq req);
}

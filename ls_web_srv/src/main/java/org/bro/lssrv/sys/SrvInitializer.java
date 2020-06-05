package org.bro.lssrv.sys;

import org.bro.lssrv.web.KcpServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SrvInitializer implements ApplicationRunner {
    @Resource
    private KcpServer kcpServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        kcpServer.start();
    }
}

package com.server.handsock;

import com.server.handsock.checks.ServerChecker;
import com.server.handsock.console.ConsolePrints;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.server.handsock")
public class HandSockServer {

    private final ServerChecker serverChecker;
    private static final ConsolePrints consolePrints = new ConsolePrints();
    public HandSockServer(ServerChecker serverChecker) {
        this.serverChecker = serverChecker;
    }

    public static void main(String[] args) {
        try {
            System.setProperty("spring.devtools.restart.enabled", "false");
            ConfigurableApplicationContext context = SpringApplication.run(HandSockServer.class, args);
            new ConsolePrints().printSuccessLog("Springboot server started!");
            context.getBean(HandSockServer.class).serverChecker.checkMysqlConnection();
            context.getBean(HandSockServer.class).serverChecker.checkRedisConnection();
            context.getBean(HandSockServer.class).serverChecker.checkDatabaseTable();
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            System.exit(0);
        }
    }
}

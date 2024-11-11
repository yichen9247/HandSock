package com.server.handsock.console;

import org.springframework.stereotype.Component;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.boot.CommandLineRunner;

@Component
public class ServerRunner implements CommandLineRunner {

    private final SocketIOServer socketIOServer;

    public ServerRunner(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    @Override
    public void run(String... args) {
        try {
            socketIOServer.start();
            new ConsolePrints().printSuccessLog("Socket.IO server started!");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
        }
    }
}
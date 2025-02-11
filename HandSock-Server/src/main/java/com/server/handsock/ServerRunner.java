package com.server.handsock;

import com.server.handsock.console.ConsolePrints;
import org.springframework.stereotype.Component;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.boot.CommandLineRunner;

@Component
public class ServerRunner implements CommandLineRunner {

    private final SocketIOServer socketIOServer;
    private final ConsolePrints consolePrints = new ConsolePrints();

    public ServerRunner(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    @Override
    public void run(String... args) {
        try {
            socketIOServer.start();
            consolePrints.printSuccessLog("Socket.IO server started!");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
        }
    }
}
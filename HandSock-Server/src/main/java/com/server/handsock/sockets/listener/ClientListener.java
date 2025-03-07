package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.sockets.handler.ClientHandler;
import com.server.handsock.utils.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ClientListener {

    private final SocketIOServer server;
    private final ClientHandler clientHandler;

    @Autowired
    public ClientListener(ClientHandler clientHandler)  {
        this.clientHandler = clientHandler;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener() {
        server.addEventListener("[CLIENT:INIT]", Map.class, (client, data, ackRequest) -> clientHandler.handleClientInit(client, ackRequest));
    }
}
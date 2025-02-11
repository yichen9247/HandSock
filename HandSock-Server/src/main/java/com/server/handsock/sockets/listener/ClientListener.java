package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.sockets.handler.ClientHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ClientListener {
    public void addEventListener(SocketIOServer server, Map<String, Object> service, Map<String, Object> clientServiceList) {
        ClientHandler clientHandler = new ClientHandler(service, clientServiceList);
        server.addEventListener("[CLIENT:INIT]", Map.class, clientHandler::handleClientInit);
    }
}
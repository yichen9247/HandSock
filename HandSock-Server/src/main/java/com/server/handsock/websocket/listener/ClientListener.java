package com.server.handsock.websocket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.websocket.handler.ClientHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ClientListener {
    public void addEventListener(SocketIOServer server, Map<String, Object> service) {
        ClientHandler clientHandler = new ClientHandler(service);
        server.addEventListener("[CLIENT:INIT]", Map.class, clientHandler::handleClientInit);
    }
}
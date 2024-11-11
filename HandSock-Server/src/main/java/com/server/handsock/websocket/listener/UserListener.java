package com.server.handsock.websocket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.websocket.handler.UserHandler;
import com.server.handsock.websocket.eventer.OnlineEvent;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserListener {
    public void addEventListener(SocketIOServer server, Map<String, Object> service, OnlineEvent onlineEvent) {
        UserHandler userHandler = new UserHandler(service);

        server.addEventListener("[USER:LOGIN]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userHandler.handleUserLogin(client, typedData, onlineEvent, ackSender);
        });

        server.addEventListener("[USER:LOGOUT]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userHandler.handleUserLogout(client, typedData, onlineEvent, ackSender);
        });

        server.addEventListener("[USER:REGISTER]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userHandler.handleUserRegister(server, client, typedData, onlineEvent, ackSender);
        });
    }
}

package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.sockets.handler.UserAuthHandler;
import com.server.handsock.sockets.eventer.OnlineEvent;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserAuthListener {
    public void addEventListener(SocketIOServer server, OnlineEvent onlineEvent, Map<String, Object> service, Map<String, Object> clientServiceList, Map<String, Object> serverServiceList) {
        UserAuthHandler userAuthHandler = new UserAuthHandler(service, clientServiceList, serverServiceList);

        server.addEventListener("[USER:LOGIN]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userAuthHandler.handleUserLogin(client, typedData, onlineEvent, ackSender);
        });

        server.addEventListener("[USER:LOGOUT]", Map.class, (client, data, ackSender) -> {
            userAuthHandler.handleUserLogout(client, server, onlineEvent, ackSender);
        });

        server.addEventListener("[USER:REGISTER]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userAuthHandler.handleUserRegister(server, client, typedData, onlineEvent, ackSender);
        });
    }
}

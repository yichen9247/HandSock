package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.sockets.handler.UserAuthHandler;
import com.server.handsock.utils.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserAuthListener {

    private final SocketIOServer server;
    private final UserAuthHandler userAuthHandler;

    @Autowired
    public UserAuthListener(UserAuthHandler userAuthHandler) {
        this.userAuthHandler = userAuthHandler;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener() {
        server.addEventListener("[USER:LOGIN]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userAuthHandler.handleUserLogin(client, typedData, ackSender);
        });

        server.addEventListener("[USER:LOGOUT]", Map.class, (client, data, ackSender) -> {
            userAuthHandler.handleUserLogout(client, server, ackSender);
        });

        server.addEventListener("[USER:REGISTER]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userAuthHandler.handleUserRegister(client, typedData, ackSender);
        });

        server.addEventListener("[USER:SCAN:LOGIN]", Map.class, (client, data, ackSender) -> {
            userAuthHandler.handleUserScanLogin(client, ackSender);
        });

        server.addEventListener("[USER:SCAN:LOGIN:STATUS]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userAuthHandler.handleGetScanLoginStatus(client, typedData, ackSender);
        });
    }
}

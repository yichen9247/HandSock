package com.server.handsock.socket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.common.utils.GlobalService;
import com.server.handsock.socket.handler.AuthHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthListener {

    private final SocketIOServer server;
    private final AuthHandler authHandler;

    @Autowired
    public AuthListener(AuthHandler authHandler) {
        this.authHandler = authHandler;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener() {
        server.addEventListener("[USER:LOGIN]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            authHandler.handleUserLogin(client, typedData, ackSender);
        });

        server.addEventListener("[USER:LOGOUT]", Map.class, (client, data, ackSender) -> {
            authHandler.handleUserLogout(client, server, ackSender);
        });

        server.addEventListener("[USER:REGISTER]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            authHandler.handleUserRegister(client, typedData, ackSender);
        });

        server.addEventListener("[USER:SCAN:LOGIN]", Map.class, (client, data, ackSender) -> {
            authHandler.handleUserScanLogin(client, ackSender);
        });

        server.addEventListener("[USER:SCAN:LOGIN:STATUS]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            authHandler.handleGetScanLoginStatus(client, typedData, ackSender);
        });
    }
}

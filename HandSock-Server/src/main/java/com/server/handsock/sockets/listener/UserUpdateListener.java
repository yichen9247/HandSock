package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.sockets.handler.UserUpdateHandler;
import com.server.handsock.utils.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserUpdateListener {

    private final SocketIOServer server;
    private final UserUpdateHandler userUpdateHandler;

    @Autowired
    public UserUpdateListener(UserUpdateHandler userUpdateHandler) {
        this.server = GlobalService.INSTANCE.getSocketIOServer();
        this.userUpdateHandler = userUpdateHandler;
    }

    public void addEventListener() {
        server.addEventListener("[EDIT:USER:NICK]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userUpdateHandler.handleEditUserInfo(client, typedData, ackSender, "USER:NICK");
        });

        server.addEventListener("[EDIT:USER:AVATAR]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userUpdateHandler.handleEditUserInfo(client, typedData, ackSender, "USER:AVATAR");
        });

        server.addEventListener("[EDIT:USER:USERNAME]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userUpdateHandler.handleEditUserInfo(client, typedData, ackSender, "USER:USERNAME");
        });

        server.addEventListener("[EDIT:USER:PASSWORD]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userUpdateHandler.handleEditUserInfo(client, typedData, ackSender, "USER:PASSWORD");
        });
    }
}

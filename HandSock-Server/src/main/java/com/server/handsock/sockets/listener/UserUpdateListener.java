package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.sockets.handler.UserUpdateHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserUpdateListener {
    public void addEventListener(SocketIOServer server, Map<String, Object> service, Map<String, Object> clientServiceList)  {
        UserUpdateHandler userUpdateHandler = new UserUpdateHandler(service, clientServiceList);

        server.addEventListener("[EDIT:USER:NICK]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userUpdateHandler.handleEditUserInfo(server, client, typedData, ackSender, "USER:NICK");
        });

        server.addEventListener("[EDIT:USER:AVATAR]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userUpdateHandler.handleEditUserInfo(server, client, typedData, ackSender, "USER:AVATAR");
        });

        server.addEventListener("[EDIT:USER:USERNAME]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userUpdateHandler.handleEditUserInfo(server, client, typedData, ackSender, "USER:USERNAME");
        });

        server.addEventListener("[EDIT:USER:PASSWORD]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userUpdateHandler.handleEditUserInfo(server, client, typedData, ackSender, "USER:PASSWORD");
        });
    }
}

package com.server.handsock.websocket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.websocket.handler.EditHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EditListener {
    public void addEventListener(SocketIOServer server, Map<String, Object> service) {
        EditHandler editHandler = new EditHandler(service);

        server.addEventListener("[EDIT:USER:NICK]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            editHandler.handleEditUserInfo(server, client, typedData, ackSender, "USER:NICK");
        });

        server.addEventListener("[EDIT:USER:AVATAR]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            editHandler.handleEditUserInfo(server, client, typedData, ackSender, "USER:AVATAR");
        });

        server.addEventListener("[EDIT:USER:USERNAME]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            editHandler.handleEditUserInfo(server, client, typedData, ackSender, "USER:USERNAME");
        });

        server.addEventListener("[EDIT:USER:PASSWORD]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            editHandler.handleEditUserInfo(server, client, typedData, ackSender, "USER:PASSWORD");
        });
    }
}

package com.server.handsock.websocket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.websocket.handler.SystemHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SystemListener {
    public void addEventListener(SocketIOServer server, Map<String, Object> service) {
        SystemHandler systemHandler = new SystemHandler(service);

        server.addEventListener("[GET:SYSTEM:CONFIG]", Map.class, (client, data, ackSender) -> {
            systemHandler.handleGetAllSystemConfig(client, ackSender);
        });

        server.addEventListener("[GET:SYSTEM:PLAYLIST]", Map.class, (client, data, ackSender) -> {
            systemHandler.handleGetSystemPlaylist(client, ackSender);
        });

        server.addEventListener("[SET:SYSTEM:CONFIG:TABOO]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            systemHandler.handleSetSystemTaboo(client, typedData, ackSender);
        });

        server.addEventListener("[SET:SYSTEM:CONFIG:UPLOAD]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            systemHandler.handleSetSystemUpload(client, typedData, ackSender);
        });

        server.addEventListener("[SET:SYSTEM:CONFIG:PLAYLIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            systemHandler.handleSetSystemPlaylist(client, typedData, ackSender);
        });
    }
}

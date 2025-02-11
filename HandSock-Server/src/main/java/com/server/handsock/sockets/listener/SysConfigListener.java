package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.sockets.handler.SysConfigHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysConfigListener {
    public void addEventListener(SocketIOServer server, Map<String, Object> service, Map<String, Object> clientServiceList, Map<String, Object> serverServiceList) {
        SysConfigHandler sysConfigHandler = new SysConfigHandler(service, clientServiceList, serverServiceList);

        server.addEventListener("[GET:SYSTEM:CONFIG]", Map.class, (client, data, ackSender) -> {
            sysConfigHandler.handleGetAllSystemConfig(client, ackSender);
        });

        server.addEventListener("[GET:SYSTEM:PLAYLIST]", Map.class, (client, data, ackSender) -> {
            sysConfigHandler.handleGetSystemPlaylist(client, ackSender);
        });

        server.addEventListener("[SET:SYSTEM:CONFIG:TABOO]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            sysConfigHandler.handleSetSystemTaboo(client, typedData, ackSender);
        });

        server.addEventListener("[SET:SYSTEM:CONFIG:UPLOAD]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            sysConfigHandler.handleSetSystemUpload(client, typedData, ackSender);
        });

        server.addEventListener("[SET:SYSTEM:CONFIG:REGISTER]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            sysConfigHandler.handleSetSystemRegister(client, typedData, ackSender);
        });

        server.addEventListener("[SET:SYSTEM:CONFIG:PLAYLIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            sysConfigHandler.handleSetSystemPlaylist(client, typedData, ackSender);
        });
    }
}

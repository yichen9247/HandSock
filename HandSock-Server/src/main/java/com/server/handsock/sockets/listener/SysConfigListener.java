package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.sockets.handler.SysConfigHandler;
import com.server.handsock.utils.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysConfigListener {

    private final SocketIOServer server;
    private final SysConfigHandler sysConfigHandler;

    @Autowired
    public SysConfigListener(SysConfigHandler sysConfigHandler) {
        this.sysConfigHandler = sysConfigHandler;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener() {
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

        server.addEventListener("[SET:SYSTEM:CONFIG:VALUE]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            sysConfigHandler.handleSetSystemConfigValue(client, typedData, ackSender);
        });
    }
}

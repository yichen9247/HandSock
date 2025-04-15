package com.server.handsock.socket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.common.data.SocketSystemConfig;
import com.server.handsock.common.utils.GlobalService;
import com.server.handsock.socket.handler.ConfigHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConfigListener {

    private final SocketIOServer server;
    private final ConfigHandler configHandler;

    @Autowired
    public ConfigListener(ConfigHandler configHandler) {
        this.configHandler = configHandler;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener() {
        server.addEventListener("[GET:SYSTEM:CONFIG]", Map.class, (client, data, ackSender) -> {
            configHandler.handleGetAllSystemConfig(client, ackSender);
        });
        server.addEventListener("[SET:SYSTEM:CONFIG:TABOO]", SocketSystemConfig.class, configHandler::handleSetSystemTaboo);
        server.addEventListener("[SET:SYSTEM:CONFIG:UPLOAD]", SocketSystemConfig.class, configHandler::handleSetSystemUpload);
        server.addEventListener("[SET:SYSTEM:CONFIG:VALUE]", SocketSystemConfig.class, configHandler::handleSetSystemConfigValue);
        server.addEventListener("[SET:SYSTEM:CONFIG:REGISTER]", SocketSystemConfig.class, configHandler::handleSetSystemRegister);
    }
}

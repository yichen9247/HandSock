package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.console.AppProperties;
import com.server.handsock.console.HandleResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CheckListener {

    private final AppProperties appProperties;

    @Autowired
    public CheckListener(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public void addEventListener(SocketIOServer server, Map<String, Object> service) {
        server.addEventListener("[CLIENT:CHECK]", Map.class, (client, data, ackSender) -> {
            if (data.get("version").equals(appProperties.getAppVersion())) {
                ackSender.sendAckData(new HandleResults().handleResultByCode(200, null, "服务正常"));
            } else ackSender.sendAckData(new HandleResults().handleResultByCode(500, null, "服务异常"));
        });
    }
}

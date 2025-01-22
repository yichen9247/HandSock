package com.server.handsock.websocket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.console.HandleResults;
import com.server.handsock.service.SocketService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CheckListener {
    SocketService socketService = new SocketService();

    public void addEventListener(SocketIOServer server, Map<String, Object> service) {
        server.addEventListener("[CLIENT:CHECK]", Map.class, (client, data, ackSender) -> {
            if (data.get("version").equals(socketService.appVersion)) {
                ackSender.sendAckData(new HandleResults().handleResultByCode(200, null, "服务正常"));
            } else ackSender.sendAckData(new HandleResults().handleResultByCode(500, null, "服务异常"));
        });
    }
}

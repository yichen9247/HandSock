package com.server.handsock.websocket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.websocket.handler.ReportHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReportListener {
    public void addEventListener(SocketIOServer server, Map<String, Object> service) {
        ReportHandler reportHandler = new ReportHandler(service);

        server.addEventListener("[REPORT:USER]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            reportHandler.handleReport(client, typedData, ackSender);
        });
    }
}

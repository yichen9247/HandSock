package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.sockets.handler.UserReportHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserReportListener {
    public void addEventListener(SocketIOServer server, Map<String, Object> service, Map<String, Object> clientServiceList)  {
        UserReportHandler userReportHandler = new UserReportHandler(service, clientServiceList);

        server.addEventListener("[REPORT:USER]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userReportHandler.handleReport(client, typedData, ackSender);
        });
    }
}

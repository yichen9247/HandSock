package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.sockets.handler.UserReportHandler;
import com.server.handsock.utils.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserReportListener {

    private final SocketIOServer server;
    private final UserReportHandler userReportHandler;

    @Autowired
    public UserReportListener(UserReportHandler userReportHandler) {
        this.userReportHandler = userReportHandler;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener() {
        server.addEventListener("[REPORT:USER]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            userReportHandler.handleReport(client, typedData, ackSender);
        });
    }
}

package com.server.handsock.socket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.common.data.SocketUserReport;
import com.server.handsock.common.utils.GlobalService;
import com.server.handsock.socket.handler.ReportHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportListener {

    private final SocketIOServer server;
    private final ReportHandler reportHandler;

    @Autowired
    public ReportListener(ReportHandler reportHandler) {
        this.reportHandler = reportHandler;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener() {
        server.addEventListener("[REPORT:USER]", SocketUserReport.class, reportHandler::handleReport);
    }
}

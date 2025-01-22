package com.server.handsock.websocket.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.service.ClientService;
import com.server.handsock.sockets.service.OP_RepoService;

import java.util.Map;

public class ReportHandler {

    private final Map<String, Object> service;
    private final HandleResults handleResults = new HandleResults();

    public ReportHandler(Map<String, Object> service) {
        this.service = service;
    }

    public void handleReport(SocketIOClient client, Map<String, Object> data, AckRequest ackSender) {
        ClientService clientService = new ClientService(service);
        if (clientService.validClientToken(client)) {
            try {
                OP_RepoService repoService = (OP_RepoService) service.get("repoService");
                Map<String, Object> call = repoService.addReport(service, clientService.getClientData(data, "sid"), Long.parseLong(clientService.getRemoteUID(client).toString()), Long.parseLong(clientService.getClientData(data, "reported_id")), clientService.getClientData(data, "reason"));
                ackSender.sendAckData(call);
                new ConsolePrints().printInfoLogV2("User Report " + call + " " + data);
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "请重新登录"));
    }
}

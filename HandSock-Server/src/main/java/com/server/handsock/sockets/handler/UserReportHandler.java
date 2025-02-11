package com.server.handsock.sockets.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.services.ClientService;
import com.server.handsock.clients.service.ClientReportService;

import java.util.Map;

public class UserReportHandler {

    private final Map<String, Object> service;
    private final Map<String, Object> clientServiceList;
    private final HandleResults handleResults = new HandleResults();

    public UserReportHandler(Map<String, Object> service, Map<String, Object> clientServiceList)  {
        this.service = service;
        this.clientServiceList = clientServiceList;
    }

    public void handleReport(SocketIOClient client, Map<String, Object> data, AckRequest ackSender) {
        ClientService clientService = new ClientService(service, clientServiceList);
        if (clientService.validClientToken(client)) {
            try {
                ClientReportService clientReportService = (ClientReportService) clientServiceList.get("clientReportService");
                Map<String, Object> call = clientReportService.addReport(service, clientService.getClientData(data, "sid"), Long.parseLong(clientService.getRemoteUID(client).toString()), Long.parseLong(clientService.getClientData(data, "reported_id")), clientService.getClientData(data, "reason"));
                ackSender.sendAckData(call);
                new ConsolePrints().printInfoLogV2("User Report " + call + " " + data);
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "请重新登录"));
    }
}

package com.server.handsock.websocket.handler;


import com.corundumstudio.socketio.AckRequest;
import com.server.handsock.console.HandleResults;
import com.server.handsock.service.ClientService;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.api.service.OU_SystemService;

import java.util.Map;

public class SystemHandler {

    private final Map<String, Object> service;

    public SystemHandler(Map<String, Object> service) {
        this.service = service;
    }

    private boolean isValidClient(SocketIOClient client) {
        ClientService clientService = new ClientService(service);
        return clientService.validClientToken(client) && clientService.getIsAdmin(client);
    }

    public void handleSetSystemTaboo(SocketIOClient client, Map<String, Object> data, AckRequest ackSender) {
        ClientService clientService = new ClientService(service);
        if (isValidClient(client)) {
            try {
                OU_SystemService OUSystemService = (OU_SystemService) service.get("systemService");
                int status = Integer.parseInt(clientService.getClientData(data, "status"));
                ackSender.sendAckData(OUSystemService.setSystemTabooStatus(status));
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(new HandleResults().handleResultByCode(403, null, "禁止访问"));
    }

    public void handleSetSystemUpload(SocketIOClient client, Map<String, Object> data, AckRequest ackSender) {
        ClientService clientService = new ClientService(service);
        if (isValidClient(client)) {
            try {
                OU_SystemService OUSystemService = (OU_SystemService) service.get("systemService");
                int status = Integer.parseInt(clientService.getClientData(data, "status"));
                ackSender.sendAckData(OUSystemService.setSystemUploadStatus(status));
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(new HandleResults().handleResultByCode(403, null, "禁止访问"));
    }

    public void handleGeyAllSystemConfig(SocketIOClient client, AckRequest ackSender) {
        ClientService clientService = new ClientService(service);
        if (isValidClient(client)) {
            try {
                OU_SystemService OUSystemService = (OU_SystemService) service.get("systemService");
                ackSender.sendAckData(OUSystemService.getAllSystemConfig());
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(new HandleResults().handleResultByCode(403, null, "禁止访问"));
    }
}

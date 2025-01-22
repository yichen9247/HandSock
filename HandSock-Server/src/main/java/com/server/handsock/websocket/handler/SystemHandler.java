package com.server.handsock.websocket.handler;


import com.corundumstudio.socketio.AckRequest;
import com.server.handsock.console.HandleResults;
import com.server.handsock.service.ClientService;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.api.service.OU_SystemService;

import java.util.Map;

public class SystemHandler {

    private final Map<String, Object> service;
    private final HandleResults handleResults = new HandleResults();

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
                String value = clientService.getClientData(data, "value");
                ackSender.sendAckData(OUSystemService.setSystemTabooStatus(value));
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
    }

    public void handleSetSystemUpload(SocketIOClient client, Map<String, Object> data, AckRequest ackSender) {
        ClientService clientService = new ClientService(service);
        if (isValidClient(client)) {
            try {
                OU_SystemService OUSystemService = (OU_SystemService) service.get("systemService");
                String value = clientService.getClientData(data, "value");
                ackSender.sendAckData(OUSystemService.setSystemUploadStatus(value));
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
    }

    public void handleSetSystemPlaylist(SocketIOClient client, Map<String, Object> data, AckRequest ackSender) {
        ClientService clientService = new ClientService(service);
        if (isValidClient(client)) {
            try {
                OU_SystemService OUSystemService = (OU_SystemService) service.get("systemService");
                String value = clientService.getClientData(data, "value");
                ackSender.sendAckData(OUSystemService.setSystemPlaylistValue(value));
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
    }

    public void handleGetSystemPlaylist(SocketIOClient client, AckRequest ackSender) {
        ClientService clientService = new ClientService(service);
        if (isValidClient(client)) {
            try {
                OU_SystemService OUSystemService = (OU_SystemService) service.get("systemService");
                ackSender.sendAckData(OUSystemService.getSystemKeyConfig("playlist"));
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
    }

    public void handleGetAllSystemConfig(SocketIOClient client, AckRequest ackSender) {
        ClientService clientService = new ClientService(service);
        if (isValidClient(client)) {
            try {
                OU_SystemService OUSystemService = (OU_SystemService) service.get("systemService");
                ackSender.sendAckData(OUSystemService.getAllSystemConfig());
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
    }
}

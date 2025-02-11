package com.server.handsock.sockets.handler;


import com.corundumstudio.socketio.AckRequest;
import com.server.handsock.console.HandleResults;
import com.server.handsock.services.ClientService;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.admin.service.ServerSystemService;

import java.util.Map;

public class SysConfigHandler {

    private final Map<String, Object> service;
    private final Map<String, Object> clientServiceList;
    private final Map<String, Object> serverServiceList;
    private final HandleResults handleResults = new HandleResults();

    public SysConfigHandler(Map<String, Object> service, Map<String, Object> clientServiceList, Map<String, Object> serverServiceList) {
        this.service = service;
        this.clientServiceList = clientServiceList;
        this.serverServiceList = serverServiceList;
    }

    private boolean isValidClient(SocketIOClient client) {
        ClientService clientService = new ClientService(service, clientServiceList);
        return clientService.validClientToken(client) && clientService.getIsAdmin(client);
    }

    public void handleSetSystemTaboo(SocketIOClient client, Map<String, Object> data, AckRequest ackSender) {
        ClientService clientService = new ClientService(service, clientServiceList);
        if (isValidClient(client)) {
            try {
                ServerSystemService serverSystemService = (ServerSystemService) serverServiceList.get("serverSystemService");
                String value = clientService.getClientData(data, "value");
                ackSender.sendAckData(serverSystemService.setSystemTabooStatus(value));
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
    }

    public void handleSetSystemUpload(SocketIOClient client, Map<String, Object> data, AckRequest ackSender) {
        ClientService clientService = new ClientService(service, clientServiceList);
        if (isValidClient(client)) {
            try {
                ServerSystemService serverSystemService = (ServerSystemService) serverServiceList.get("serverSystemService");
                String value = clientService.getClientData(data, "value");
                ackSender.sendAckData(serverSystemService.setSystemUploadStatus(value));
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
    }

    public void handleSetSystemRegister(SocketIOClient client, Map<String, Object> data, AckRequest ackSender) {
        ClientService clientService = new ClientService(service, clientServiceList);
        if (isValidClient(client)) {
            try {
                ServerSystemService serverSystemService = (ServerSystemService) serverServiceList.get("systemService");
                String value = clientService.getClientData(data, "value");
                ackSender.sendAckData(serverSystemService.setSystemRegisterStatus(value));
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
    }

    public void handleSetSystemPlaylist(SocketIOClient client, Map<String, Object> data, AckRequest ackSender) {
        ClientService clientService = new ClientService(service, clientServiceList);
        if (isValidClient(client)) {
            try {
                ServerSystemService serverSystemService = (ServerSystemService) serverServiceList.get("serverSystemService");
                String value = clientService.getClientData(data, "value");
                ackSender.sendAckData(serverSystemService.setSystemPlaylistValue(value));
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
    }

    public void handleGetSystemPlaylist(SocketIOClient client, AckRequest ackSender) {
        ClientService clientService = new ClientService(service, clientServiceList);
        if (isValidClient(client)) {
            try {
                ServerSystemService serverSystemService = (ServerSystemService) serverServiceList.get("serverSystemService");
                ackSender.sendAckData(serverSystemService.getSystemKeyConfig("playlist"));
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
    }

    public void handleGetAllSystemConfig(SocketIOClient client, AckRequest ackSender) {
        ClientService clientService = new ClientService(service, clientServiceList);
        if (isValidClient(client)) {
            try {
                ServerSystemService serverSystemService = (ServerSystemService) serverServiceList.get("serverSystemService");
                ackSender.sendAckData(serverSystemService.getAllSystemConfig());
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
    }
}

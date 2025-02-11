package com.server.handsock.sockets.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.admin.service.ServerSystemService;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.console.UtilityService;
import com.server.handsock.services.ClientService;
import com.server.handsock.services.TokenService;
import com.server.handsock.clients.service.ClientUserService;
import com.server.handsock.sockets.eventer.OnlineEvent;

import java.util.Map;

public class UserAuthHandler {

    private final Map<String, Object> service;
    private final Map<String, Object> serverServiceList;
    private final Map<String, Object> clientServiceList;
    private final UtilityService utilityService = new UtilityService();
    private final HandleResults handleResults = new HandleResults();

    public UserAuthHandler(Map<String, Object> service, Map<String, Object> clientServiceList, Map<String, Object> serverServiceList) {
        this.service = service;
        this.clientServiceList = clientServiceList;
        this.serverServiceList = serverServiceList;
    }

    public void handleUserLogin(SocketIOClient client, Map<String, Object> data, OnlineEvent onlineEvent, AckRequest ackSender) {
        ClientService clientService = new ClientService(service, clientServiceList);
        try {
            if (!onlineEvent.clientList.contains(utilityService.encodeStringToMD5(client.getSessionId().toString()))) {
                ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
            } else {
                ClientUserService clientUserService = (ClientUserService) clientServiceList.get("clientUserService");
                ackSender.sendAckData(clientUserService.loginUser(clientService.getClientData(data, "username"), clientService.getClientData(data, "password"), clientService.getRemoteAddress(client)));
            }
        } catch (Exception e) {
            clientService.handleException(e, ackSender);
        }
    }

    public void handleUserLogout(SocketIOClient client, SocketIOServer server, OnlineEvent onlineEvent, AckRequest ackSender) {
        ClientService clientService = new ClientService(service, clientServiceList);
        if (clientService.validClientToken(client)) {
            try {
                if (!onlineEvent.clientList.contains(utilityService.encodeStringToMD5(client.getSessionId().toString()))) {
                    ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
                } else {
                    TokenService tokenService = (TokenService) service.get("tokenService");
                    tokenService.removeUserToken(clientService.getRemoteUID(client));
                    onlineEvent.sendUserDisconnect(server, client, clientService.getRemoteAddress(client));
                    new ConsolePrints().printInfoLogV2("User Logout " + clientService.getRemoteAddress(client) + " " + clientService.getRemoteUID(client));
                }
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "已是退出状态"));
    }

    public void handleUserRegister(SocketIOServer server, SocketIOClient client, Map<String, Object> data, OnlineEvent onlineEvent, AckRequest ackSender) {
        ClientService clientService = new ClientService(service, clientServiceList);
        try {
            if (!onlineEvent.clientList.contains(utilityService.encodeStringToMD5(client.getSessionId().toString()))) {
                ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
            } else {
                ServerSystemService serverSystemService = (ServerSystemService) serverServiceList.get("serverSystemService");
                if (serverSystemService.getSystemKeyStatus("register")) {
                    ClientUserService clientUserService = (ClientUserService) clientServiceList.get("clientUserService");
                    Map<String, Object> result = clientUserService.registerUser(clientService.getClientData(data, "username"), clientService.getClientData(data, "password"), clientService.getRemoteAddress(client));
                    ackSender.sendAckData(result);
                    if (Integer.parseInt(clientService.getClientData(result, "code")) == 200) utilityService.sendGlobalMessage(server, "[RE:USER:ALL]", null);
                } else ackSender.sendAckData(handleResults.handleResultByCode(402, null, "当前禁止注册"));
            }
        } catch (Exception e) {
            clientService.handleException(e, ackSender);
        }
    }
}

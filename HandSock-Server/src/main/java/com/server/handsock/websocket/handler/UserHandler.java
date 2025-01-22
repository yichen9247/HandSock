package com.server.handsock.websocket.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.sockets.service.OP_UserService;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.console.MD5Encoder;
import com.server.handsock.service.ClientService;
import com.server.handsock.service.TokenService;
import com.server.handsock.websocket.eventer.OnlineEvent;
import com.server.handsock.console.MessageUtils;

import java.util.Map;

public class UserHandler {

    private final Map<String, Object> service;
    private final HandleResults handleResults = new HandleResults();

    public UserHandler(Map<String, Object> service) {
        this.service = service;
    }

    public void handleUserLogin(SocketIOClient client, Map<String, Object> data, OnlineEvent onlineEvent, AckRequest ackSender) {
        ClientService clientService = new ClientService(service);
        try {
            if (!onlineEvent.clientList.contains(new MD5Encoder().toMD5(client.getSessionId().toString()))) {
                ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
            } else {
                OP_UserService OPUserService = (OP_UserService) service.get("userService");
                ackSender.sendAckData(OPUserService.loginUser(clientService.getClientData(data, "username"), clientService.getClientData(data, "password"), clientService.getRemoteAddress(client)));
            }
        } catch (Exception e) {
            clientService.handleException(e, ackSender);
        }
    }

    public void handleUserLogout(SocketIOClient client, Map<String, Object> data, OnlineEvent onlineEvent, AckRequest ackSender) {
        ClientService clientService = new ClientService(service);
        if (clientService.validClientToken(client)) {
            try {
                if (!onlineEvent.clientList.contains(new MD5Encoder().toMD5(client.getSessionId().toString()))) {
                    ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
                } else {
                    TokenService tokenService = (TokenService) service.get("tokenService");
                    tokenService.removeUserToken(clientService.getRemoteUID(client));
                    new ConsolePrints().printInfoLogV2("User Logout " + clientService.getRemoteAddress(client) + " " + clientService.getRemoteUID(client));
                }
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "请重新登录"));
    }

    public void handleUserRegister(SocketIOServer server, SocketIOClient client, Map<String, Object> data, OnlineEvent onlineEvent, AckRequest ackSender) {
        ClientService clientService = new ClientService(service);
        try {
            if (!onlineEvent.clientList.contains(new MD5Encoder().toMD5(client.getSessionId().toString()))) {
                ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
            } else {
                OP_UserService OPUserService = (OP_UserService) service.get("userService");
                Map<String, Object> result = OPUserService.registerUser(clientService.getClientData(data, "username"), clientService.getClientData(data, "password"), clientService.getRemoteAddress(client));
                ackSender.sendAckData(result);
                if (Integer.parseInt(clientService.getClientData(result, "code")) == 200) new MessageUtils().sendGlobalMessage(server, "[RE:USER:ALL]", null);
            }
        } catch (Exception e) {
            clientService.handleException(e, ackSender);
        }
    }
}

package com.server.handsock.websocket.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.sockets.service.OP_UserService;
import com.server.handsock.console.HandleResults;
import com.server.handsock.service.ClientService;

import java.util.HashMap;
import java.util.Map;

public class ClientHandler {

    private final Map<String, Object> service;
    private final HandleResults handleResults = new HandleResults();

    public ClientHandler(Map<String, Object> service) {
        this.service = service;
    }

    public void handleClientInit(SocketIOClient client, Map<String, Object> data, AckRequest ackRequest) {
        ClientService clientService = new ClientService(service);
        try {
            if (clientService.validClientToken(client)) {
                Map<String, Object> result = new HashMap<>();
                OP_UserService OPUserService = (OP_UserService) service.get("userService");
                result.put("userinfo", OPUserService.queryUserInfo(clientService.getRemoteUID(client)));
                ackRequest.sendAckData(handleResults.handleResultByCode(200, result, "获取成功"));
            } else ackRequest.sendAckData(handleResults.handleResultByCode(403, null, "请重新登录"));
        } catch (Exception e) {
            clientService.handleException(e, ackRequest);
        }
    }

}

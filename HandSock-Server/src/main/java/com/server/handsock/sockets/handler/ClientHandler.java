package com.server.handsock.sockets.handler;

import com.corundumstudio.socketio.AckRequest;
import com.server.handsock.console.HandleResults;
import com.server.handsock.services.ClientService;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.clients.service.ClientUserService;

import java.util.HashMap;
import java.util.Map;

public class ClientHandler {

    private final Map<String, Object> service;
    private final Map<String, Object> clientServiceList;
    private final HandleResults handleResults = new HandleResults();

    public ClientHandler(Map<String, Object> service, Map<String, Object> clientServiceList) {
        this.service = service;
        this.clientServiceList = clientServiceList;
    }

    public void handleClientInit(SocketIOClient client, Map<String, Object> data, AckRequest ackRequest) {
        ClientService clientService = new ClientService(service, clientServiceList);
        try {
            if (clientService.validClientToken(client)) {
                Map<String, Object> result = new HashMap<>();
                ClientUserService clientUserService = (ClientUserService) clientServiceList.get("clientUserService");
                result.put("userinfo", clientUserService.queryUserInfo(clientService.getRemoteUID(client)));
                ackRequest.sendAckData(handleResults.handleResultByCode(200, result, "获取成功"));
            } else ackRequest.sendAckData(handleResults.handleResultByCode(403, null, "请重新登录"));
        } catch (Exception e) {
            clientService.handleException(e, ackRequest);
        }
    }

}

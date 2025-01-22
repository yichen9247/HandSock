package com.server.handsock.websocket.handler;

import com.corundumstudio.socketio.AckRequest;
import com.server.handsock.console.MessageUtils;
import com.server.handsock.console.HandleResults;
import com.server.handsock.service.CacheService;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.service.ClientService;
import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.admin.service.OR_ChanService;
import com.server.handsock.admin.service.OR_UserService;
import com.server.handsock.api.service.OU_SystemService;
import com.server.handsock.sockets.service.OP_ChatService;

import java.util.Map;

public class SendHandler {

    private static final String TYPE_KEY = "type";
    private static final String CODE_KEY = "code";
    private static final String CONTENT_KEY = "content";

    private final Map<String, Object> service;
    private final ClientService clientService;
    private final CacheService cacheService;
    private final OR_UserService orUserService;
    private final OR_ChanService orChanService;
    private final OU_SystemService ouSystemService;
    private final HandleResults handleResults = new HandleResults();

    public SendHandler(Map<String, Object> service) {
        this.service = service;
        this.clientService = new ClientService(service);
        this.cacheService = (CacheService) service.get("cacheService");
        this.orUserService = (OR_UserService) service.get("or_userService");
        this.orChanService = (OR_ChanService) service.get("or_chanService");
        this.ouSystemService = (OU_SystemService) service.get("systemService");
    }

    public void handleSendMessage(SocketIOServer server, SocketIOClient client, Map<String, Object> data, AckRequest ackRequest) {
        if (!cacheService.validRedisMessageCache(clientService.getRemoteUID(client))) {
            ackRequest.sendAckData(new HandleResults().handleResultByCode(402, null, "操作频率过快"));
            return;
        }

        if (!orUserService.getUserInnerStatus(clientService.getRemoteUID(client))) {
            ackRequest.sendAckData(new HandleResults().handleResultByCode(402, null, "未查询到用户"));
            return;
        }

        if (!orChanService.getChanOpenStatus(clientService.getRemoteGID(client))) {
            ackRequest.sendAckData(new HandleResults().handleResultByCode(402, null, "该频道未开启"));
            return;
        }

        if (ouSystemService.getSystemKeyStatus("taboo") && !clientService.getIsAdmin(client)) {
            ackRequest.sendAckData(new HandleResults().handleResultByCode(402, null, "全频禁言开启中"));
            return;
        }

        if (orUserService.getUserTabooStatus(clientService.getRemoteUID(client)) && !clientService.getIsAdmin(client)) {
            ackRequest.sendAckData(new HandleResults().handleResultByCode(402, null, "你正在被禁言中"));
            return;
        }

        if (!clientService.validClientToken(client)) {
            sendAckData(ackRequest, handleResults.handleResultByCode(403, null, "登录状态已失效"));
            return;
        }

        try {
            String content = clientService.getClientData(data, CONTENT_KEY);
            OP_ChatService OPChatService = (OP_ChatService) service.get("chatService");
            Map<String, Object> result = OPChatService.insertChatMessage(clientService.getClientData(data, TYPE_KEY), clientService.getRemoteUID(client), clientService.getRemoteGID(client), clientService.getRemoteAddress(client), content);
            sendAckData(ackRequest, result);
            if (isSuccessResult(result)) new MessageUtils().sendGlobalMessage(server, "[MESSAGE]", result.get("data"));
        } catch (Exception e) {
            handleException(e, ackRequest);
        }
    }

    private void handleException(Exception e, AckRequest ackRequest) {
        clientService.handleException(e, ackRequest);
    }

    private boolean isSuccessResult(Map<String, Object> result) {
        return Integer.parseInt(clientService.getClientData(result, CODE_KEY)) == 200;
    }

    private void sendAckData(AckRequest ackRequest, Map<String, Object> result) {
        ackRequest.sendAckData(result);
    }
}
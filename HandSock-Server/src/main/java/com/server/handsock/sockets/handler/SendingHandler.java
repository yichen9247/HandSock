package com.server.handsock.sockets.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.handsock.admin.service.ServerSystemService;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.console.UtilityService;
import com.server.handsock.services.CacheService;
import com.server.handsock.services.ClientService;
import com.server.handsock.clients.service.ClientChannelService;
import com.server.handsock.clients.service.ClientChatService;
import com.server.handsock.clients.service.ClientUserService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Getter @Setter
public class SendingHandler {

    private static final String TYPE_KEY = "type";
    private static final String CODE_KEY = "code";
    private static final String CONTENT_KEY = "content";

    private final CacheService cacheService;
    private final ClientService clientService;
    private final ClientUserService clientUserService;
    private final Map<String, Object> clientServiceList;
    private final ServerSystemService serverSystemService;
    private final ClientChannelService clientChannelService;

    private final UtilityService utilityService = new UtilityService();
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    public SendingHandler(Map<String, Object> service, Map<String, Object> clientServiceList, Map<String, Object> serverServiceList) {
        this.clientServiceList = clientServiceList;
        this.cacheService = (CacheService) service.get("cacheService");
        this.clientService = new ClientService(service, clientServiceList);
        this.clientUserService = (ClientUserService) clientServiceList.get("clientUserService");
        this.serverSystemService = (ServerSystemService) serverServiceList.get("serverSystemService");
        this.clientChannelService = (ClientChannelService) clientServiceList.get("clientChannelService");
    }

    public void handleSendMessage(SocketIOServer server, SocketIOClient client, Map<String, Object> data, AckRequest ackRequest) {
        if (!cacheService.validRedisMessageCache(clientService.getRemoteUID(client))) {
            ackRequest.sendAckData(new HandleResults().handleResultByCode(402, null, "操作频率过快"));
            return;
        }

        if (!clientUserService.getUserInnerStatus(clientService.getRemoteUID(client))) {
            ackRequest.sendAckData(new HandleResults().handleResultByCode(402, null, "未查询到用户"));
            return;
        }

        if (!clientChannelService.getChanOpenStatus(clientService.getRemoteGID(client))) {
            ackRequest.sendAckData(new HandleResults().handleResultByCode(402, null, "该频道暂未开启"));
            return;
        }

        if (serverSystemService.getSystemKeyStatus("taboo") && !clientService.getIsAdmin(client)) {
            ackRequest.sendAckData(new HandleResults().handleResultByCode(402, null, "全频禁言开启中"));
            return;
        }

        if (clientUserService.getUserTabooStatus(clientService.getRemoteUID(client)) && !clientService.getIsAdmin(client)) {
            ackRequest.sendAckData(new HandleResults().handleResultByCode(402, null, "你正在被禁言中"));
            return;
        }

        if (!clientService.validClientToken(client)) {
            sendAckData(ackRequest, handleResults.handleResultByCode(403, null, "登录状态已失效"));
            return;
        }

        try {
            ClientChatService clientChatService = (ClientChatService) clientServiceList.get("clientChatService");
            String content = stripHtmlTags(clientService.getClientData(data, CONTENT_KEY));
            Map<String, Object> result = clientChatService.insertChatMessage(clientService.getClientData(data, TYPE_KEY), clientService.getRemoteUID(client), clientService.getRemoteGID(client), clientService.getRemoteAddress(client), content);
            sendAckData(ackRequest, result);
            if (isSuccessResult(result)) utilityService.sendGlobalMessage(server, "[MESSAGE]", result.get("data"));
        } catch (Exception e) {
            handleException(e, ackRequest);
        }
    }

    ObjectMapper objectMapper = new ObjectMapper();

    // 使用正则表达式对文本进行过滤处理
    private String stripHtmlTags(String html) {
        if (html == null) return null;
        return Pattern.compile("<.*?>", Pattern.DOTALL).matcher(html).replaceAll("");
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

    private static final Map<Long, List<String>> userMessageHistory = new ConcurrentHashMap<>();
}
package com.server.handsock.sockets.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.handsock.console.AIProperties;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.services.CacheService;
import com.server.handsock.services.ClientService;
import com.server.handsock.clients.man.ClientChatManage;
import com.server.handsock.clients.mod.ClientChatModel;
import com.server.handsock.admin.service.ServerSystemService;
import com.server.handsock.clients.service.ClientChannelService;
import com.server.handsock.clients.service.ClientUserService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class AIChatHandler {

    private final CacheService cacheService;
    private final ClientService clientService;
    private final ClientUserService clientUserService;
    private final ServerSystemService serverSystemService;
    private final ClientChannelService clientChannelService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    private static final String CONTENT_KEY = "content";
    private static final String REQUEST_STATUS = "正在请求中";
    private static final String DEFAULT_MESSAGE = "暂无AI能力相关权限，请前往任意非AI频道发送【handsock apply-ai】以开启AI能力";

    public AIChatHandler(Map<String, Object> service, Map<String, Object> clientServiceList, Map<String, Object> serverServiceList) {
        this.cacheService = (CacheService) service.get("cacheService");
        this.clientService = new ClientService(service, clientServiceList);
        this.clientUserService = (ClientUserService) clientServiceList.get("clientUserService");
        this.serverSystemService = (ServerSystemService) serverServiceList.get("serverSystemService");
        this.clientChannelService = (ClientChannelService) clientServiceList.get("clientChannelService");
    }

    public void handleAIChatMessage(SocketIOClient client, Map<String, Object> data, AckRequest ackRequest, AIProperties aiProperties) {
        if (!cacheService.validRedisMessageCache(clientService.getRemoteUID(client))) {
            sendAckData(ackRequest, handleResults.handleResultByCode(402, null, "操作频率过快"));
            return;
        }

        if (!clientUserService.getUserInnerStatus(clientService.getRemoteUID(client))) {
            sendAckData(ackRequest, handleResults.handleResultByCode(402, null, "未查询到用户"));
            return;
        }

        if (!clientChannelService.getChanOpenStatus(clientService.getRemoteGID(client))) {
            sendAckData(ackRequest, handleResults.handleResultByCode(402, null, "该频道暂未开启"));
            return;
        }

        if (serverSystemService.getSystemKeyStatus("taboo") && !clientService.getIsAdmin(client)) {
            sendAckData(ackRequest, handleResults.handleResultByCode(402, null, "全频禁言开启中"));
            return;
        }

        if (clientUserService.getUserTabooStatus(clientService.getRemoteUID(client)) && !clientService.getIsAdmin(client)) {
            sendAckData(ackRequest, handleResults.handleResultByCode(402, null, "你正在被禁言中"));
            return;
        }

        if (!clientService.validClientToken(client)) {
            sendAckData(ackRequest, handleResults.handleResultByCode(403, null, "登录状态已失效"));
            return;
        }

        try {
            String content = stripHtmlTags(clientService.getClientData(data, CONTENT_KEY));
            ClientChatModel clientChannelDao = new ClientChatModel();
            ClientChatManage clientChatManage = new ClientChatManage();

            Map<String, Object> userResult = clientChatManage.insertChatMessage(clientChannelDao, "text", clientService.getRemoteUID(client), clientService.getRemoteGID(client), clientService.getRemoteAddress(client), content);
            sendAckData(ackRequest, handleResults.handleResultByCode(200, userResult, "请求成功"));

            Thread.sleep(800);
            boolean hasAiAuth = clientService.hasAiAuthorization(client);
            String message = hasAiAuth ? REQUEST_STATUS : DEFAULT_MESSAGE;
            Map<String, Object> aiResult = clientChatManage.insertChatMessage(clientChannelDao, "text", clientUserService.getRobotInnerStatus(), clientService.getRemoteGID(client), "none", message);
            sendEventWithResult(client, aiResult, "请求成功");
            if (hasAiAuth) sendAIRequest(client, aiResult.get("sid").toString(), clientService.getRemoteUID(client), content, aiProperties, ackRequest);
        } catch (Exception e) {
            handleException(e, ackRequest);
        }
    }

    private void sendEventWithResult(SocketIOClient client, Map<String, Object> aiResult, String statusMessage) {
        client.sendEvent("[AI:CHAT:CREATE:MESSAGE]", handleResults.handleResultByCode(200, new HashMap<String, Object>() {{
            put("event", "CREATE-MESSAGE");
            put("result", aiResult);
        }}, statusMessage));
    }

    // 使用正则表达式对文本进行过滤处理
    private String stripHtmlTags(String html) {
        if (html == null) return null;
        return Pattern.compile("<.*?>", Pattern.DOTALL).matcher(html).replaceAll("");
    }

    private void handleException(Exception e, AckRequest ackRequest) {
        consolePrints.printErrorLog(e);
        clientService.handleException(e, ackRequest);
    }

    private void sendAckData(AckRequest ackRequest, Map<String, Object> result) {
        ackRequest.sendAckData(result);
    }

    private static final Map<Long, List<String>> userMessageHistory = new ConcurrentHashMap<>();

    private void sendAIRequest(SocketIOClient client, String sid, Long uid, String userContent, AIProperties aiProperties, AckRequest ackRequest) {
        WebClient webClient = WebClient.builder()
                .baseUrl(aiProperties.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + aiProperties.getToken())
                .build();

        userMessageHistory.computeIfAbsent(uid, k -> new ArrayList<>()).add(userContent);
        String roleString = "{\"role\": \"system\",\"content\": \"设定：你是HandSock聊天室的 AI 助手，回复不能超过150字。开启深度思考，请用 <think>和</think> 包裹你的内部推理过程，推荐过程尽量少一点，最终回复要简洁自然。\"}";
        StringBuilder roleStringBuilder = new StringBuilder(roleString);
        for (String msg : userMessageHistory.get(uid)) roleStringBuilder.append(",{\"role\": \"system\",\"name\": \"").append(uid).append("\",\"content\": \"").append(msg).append("\"}");
        String roleStringWithHistory = roleStringBuilder.toString();
        consolePrints.printInfoLogV1(roleStringWithHistory);

        String requestBody = "{\n" +
                "    \"model\": \"" + aiProperties.getModel() + "\",\n" +
                "    \"messages\": [\n" + roleStringWithHistory +
                "    ],\n" +
                "    \"response_format\": {\n" +
                "        \"type\": \"text\"\n" +
                "    },\n" +
                "    \"stream\": true\n" +
                "}";

        Flux<String> responseFlux = webClient.post()
                .uri(aiProperties.getPath())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class);

        responseFlux.subscribe(
                line -> handleResponseLine(client, sid, line),
                error -> {
                    consolePrints.printErrorLog(error.getMessage());
                    client.sendEvent("[AI:CHAT:CREATE:MESSAGE]", handleResults.handleResultByCode(200, new HashMap<String, Object>() {{
                        put("event", "PUSH-STREAM");
                        put("eventId", sid);
                        put("content", "请求失败，请查看日志");
                    }}, "请求成功"));
                },
                () -> consolePrints.printInfoLogV2("AI Request Complete")
        );
    }

    private void handleResponseLine(SocketIOClient client, String sid, String line) {
        try {
            if (!Objects.equals(line, "[DONE]")) {
                String content = objectMapper.readTree(line).path("choices").get(0).path("delta").path("content").asText();
                client.sendEvent("[AI:CHAT:CREATE:MESSAGE]", handleResults.handleResultByCode(200, new HashMap<String, Object>() {{
                    put("event", "PUSH-STREAM");
                    put("eventId", sid);
                    put("content", content);
                }}, "请求成功"));
            }
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
        }
    }
}
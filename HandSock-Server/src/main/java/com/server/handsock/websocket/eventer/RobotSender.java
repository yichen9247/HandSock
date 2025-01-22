package com.server.handsock.websocket.eventer;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.admin.service.OR_ChanService;
import com.server.handsock.admin.service.OR_UserService;
import com.server.handsock.api.service.OU_SystemService;
import com.server.handsock.console.MessageUtils;
import com.server.handsock.service.CacheService;
import com.server.handsock.service.ClientService;
import com.server.handsock.sockets.service.OP_ChatService;

import java.util.Map;

public class RobotSender {

    private static final String TYPE_KEY = "type";
    private static final String CODE_KEY = "code";
    private static final String CONTENT_KEY = "content";

    private final Map<String, Object> service;
    private final ClientService clientService;
    private final CacheService cacheService;
    private final OR_UserService orUserService;
    private final OR_ChanService orChanService;
    private final OU_SystemService ouSystemService;

    public RobotSender(Map<String, Object> service) {
        this.service = service;
        this.clientService = new ClientService(service);
        this.cacheService = (CacheService) service.get("cacheService");
        this.orUserService = (OR_UserService) service.get("or_userService");
        this.orChanService = (OR_ChanService) service.get("or_chanService");
        this.ouSystemService = (OU_SystemService) service.get("systemService");
    }

    public void handleSendMessageOnBot(SocketIOServer server, SocketIOClient client, Object dataEvent, AckRequest ackRequest) {
        if (isValidMessage(client)) return;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataEvent;

            String content = clientService.getClientData(data, CONTENT_KEY);
            OP_ChatService OPChatService = (OP_ChatService) service.get("chatService");
            RobotsEvent robotsEvent = (RobotsEvent) service.get("robotsEvent");

            @SuppressWarnings("unchecked")
            Map<String, Object> authToken = (Map<String, Object>) client.getHandshakeData().getAuthToken();

            String robotReturn = robotsEvent.handleRobotCommand(client, content, server);
            if (robotReturn != null) {
                Map<String, Object> robotResult = OPChatService.insertChatMessage(clientService.getClientData(data, TYPE_KEY), orUserService.getRobotInnerStatus(), Long.parseLong(clientService.getClientData(authToken, "gid")), "none", robotReturn);
                sendGlobalMessageIfSuccess(robotResult, server);
            }
        } catch (Exception e) {
            handleException(e, ackRequest);
        } finally {
            cacheService.writeRedisMessageCache(clientService.getRemoteUID(client));
        }
    }

    private void handleException(Exception e, AckRequest ackRequest) {
        clientService.handleException(e, ackRequest);
    }

    private boolean isSuccessResult(Map<String, Object> result) {
        return Integer.parseInt(clientService.getClientData(result, CODE_KEY)) == 200;
    }

    private void sendGlobalMessageIfSuccess(Map<String, Object> result, SocketIOServer server) throws InterruptedException {
        if (isSuccessResult(result)) {
            Thread.sleep(500);
            new MessageUtils().sendGlobalMessage(server, "[MESSAGE]", result.get("data"));
        }
    }

    private boolean isValidMessage(SocketIOClient client) {
        return !cacheService.validRedisMessageCache(clientService.getRemoteUID(client)) || orUserService.getRobotInnerStatus() == null || !orUserService.getUserInnerStatus(clientService.getRemoteUID(client)) || !orChanService.getChanOpenStatus(clientService.getRemoteGID(client)) || ouSystemService.getSystemKeyStatus("taboo") && !clientService.getIsAdmin(client) || orUserService.getUserTabooStatus(clientService.getRemoteUID(client)) && !clientService.getIsAdmin(client) || !clientService.validClientToken(client);
    }
}

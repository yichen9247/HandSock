package com.server.handsock.sockets.eventer;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.admin.service.ServerSystemService;
import com.server.handsock.console.UtilityService;
import com.server.handsock.services.CacheService;
import com.server.handsock.services.ClientService;
import com.server.handsock.clients.service.ClientChannelService;
import com.server.handsock.clients.service.ClientChatService;
import com.server.handsock.clients.service.ClientUserService;

import java.util.Map;

public class RobotSender {

    private static final String TYPE_KEY = "type";
    private static final String CODE_KEY = "code";
    private static final String CONTENT_KEY = "content";

    private final UtilityService utilityService = new UtilityService();

    private final CacheService cacheService;
    private final Map<String, Object> service;
    private final ClientService clientService;
    private final ClientChatService clientChatService;
    private final ClientUserService clientUserService;
    private final Map<String, Object> clientServiceList;
    private final ServerSystemService serverSystemService;
    private final ClientChannelService clientChannelService;

    public RobotSender(Map<String, Object> service, Map<String, Object> clientServiceList, Map<String, Object> serverServiceList) {
        this.service = service;
        this.clientServiceList = clientServiceList;
        this.cacheService = (CacheService) service.get("cacheService");
        this.clientService = new ClientService(service, clientServiceList);
        this.clientChatService = (ClientChatService) clientServiceList.get("clientChatService");
        this.clientUserService = (ClientUserService) clientServiceList.get("clientUserService");
        this.serverSystemService = (ServerSystemService) serverServiceList.get("serverSystemService");
        this.clientChannelService = (ClientChannelService) clientServiceList.get("clientChannelService");
    }

    public void handleSendMessageOnBot(SocketIOServer server, SocketIOClient client, Object dataEvent, AckRequest ackRequest) {
        if (isValidMessage(client)) return;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataEvent;
            String content = clientService.getClientData(data, CONTENT_KEY);
            RobotsEvent robotsEvent = (RobotsEvent) service.get("robotsEvent");

            @SuppressWarnings("unchecked")
            Map<String, Object> authToken = (Map<String, Object>) client.getHandshakeData().getAuthToken();

            String robotReturn = robotsEvent.handleRobotCommand(client, server, content, service, clientServiceList);
            if (robotReturn != null) {
                Thread.sleep(800);
                Map<String, Object> robotResult = clientChatService.insertChatMessage(clientService.getClientData(data, TYPE_KEY), clientUserService.getRobotInnerStatus(), Long.parseLong(clientService.getClientData(authToken, "gid")), "none", robotReturn);
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
        if (isSuccessResult(result)) utilityService.sendGlobalMessage(server, "[MESSAGE]", result.get("data"));
    }

    private boolean isValidMessage(SocketIOClient client) {
        return !cacheService.validRedisMessageCache(clientService.getRemoteUID(client)) || clientUserService.getRobotInnerStatus() == null || !clientUserService.getUserInnerStatus(clientService.getRemoteUID(client)) || !clientChannelService.getChanOpenStatus(clientService.getRemoteGID(client)) || serverSystemService.getSystemKeyStatus("taboo") && !clientService.getIsAdmin(client) || clientUserService.getUserTabooStatus(clientService.getRemoteUID(client)) && !clientService.getIsAdmin(client) || !clientService.validClientToken(client);
    }
}

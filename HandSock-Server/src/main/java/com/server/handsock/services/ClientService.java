package com.server.handsock.services;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.clients.service.ClientUserService;

import java.util.Map;

public class ClientService {

    private final Map<String, Object> service;
    private final Map<String, Object> clientServiceList;
    private static final String ERROR_MSG_UNKNOWN = "未知错误";;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private static final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;

    public ClientService(Map<String, Object> service, Map<String, Object> clientServiceList) {
        this.service = service;
        this.clientServiceList = clientServiceList;
    }

    public void handleException(Exception e, AckRequest ackSender) {
        consolePrints.printErrorLog(e);
        ackSender.sendAckData(new HandleResults().handleResultByCode(HTTP_STATUS_INTERNAL_SERVER_ERROR, null, ERROR_MSG_UNKNOWN));
    }

    // 获取客户端数据
    public String getClientData(Map<String, Object> data, String key) {
        return data.get(key).toString();
    }

    // 判断用户是否是管理员
    public Boolean getIsAdmin(SocketIOClient client) {
        ClientUserService clientUserService = (ClientUserService) clientServiceList.get("clientUserService");
        return clientUserService.queryUserInfo(getRemoteUID(client)).getIsAdmin() == 1;
    }

    // 判断用户是否是拥有AI能力相关权限
    public Boolean hasAiAuthorization(SocketIOClient client) {
        ClientUserService clientUserService = (ClientUserService) clientServiceList.get("clientUserService");
        return clientUserService.queryUserInfo(getRemoteUID(client)).getAiAuth() == 1;
    }

    // 获取用户uid
    public Long getRemoteUID(SocketIOClient client) {
        @SuppressWarnings("unchecked")
        Map<String, Object> authToken = (Map<String, Object>) client.getHandshakeData().getAuthToken();
        return Long.parseLong(authToken.get("uid").toString());
    }

    // 获取频道gid
    public Long getRemoteGID(SocketIOClient client) {
        @SuppressWarnings("unchecked")
        Map<String, Object> authToken = (Map<String, Object>) client.getHandshakeData().getAuthToken();
        return Long.parseLong(authToken.get("gid").toString());
    }

    // 获取客户端IP地址
    public String getRemoteAddress(SocketIOClient client) {
        return client.getRemoteAddress().toString().substring(1);
    }

    // 验证客户端Token是否有效
    public Boolean validClientToken(SocketIOClient client) {
        try {
            ClientUserService clientUserService = (ClientUserService) clientServiceList.get("clientUserService");

            @SuppressWarnings("unchecked")
            Map<String, Object> authToken = (Map<String, Object>) client.getHandshakeData().getAuthToken();

            long uid = Long.parseLong(authToken.get("uid").toString());
            TokenService tokenService = (TokenService) service.get("tokenService");
            return (tokenService.validUserToken(uid, authToken.get("token").toString()) && clientUserService.checkUserLogin(uid));
        } catch (Exception e) {
            return false;
        }
    }
}

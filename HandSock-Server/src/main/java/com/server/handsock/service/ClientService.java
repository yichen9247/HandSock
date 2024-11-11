package com.server.handsock.service;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.sockets.service.OP_UserService;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;

import java.util.Map;

public class ClientService {

    private final Map<String, Object> service;
    private static final String ERROR_MSG_UNKNOWN = "未知错误";
    private static final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;
    private final ConsolePrints consolePrints = new ConsolePrints();

    public ClientService(Map<String, Object> service) {
        this.service = service;
    }

    public void handleException(Exception e, AckRequest ackSender) {
        consolePrints.printErrorLog(e);
        ackSender.sendAckData(new HandleResults().handleResultByCode(HTTP_STATUS_INTERNAL_SERVER_ERROR, null, ERROR_MSG_UNKNOWN));
    }

    public String getClientData(Map<String, Object> data, String key) {
        return data.get(key).toString();
    }

    public Boolean getIsAdmin(SocketIOClient client) {
        OP_UserService OPUserService = (OP_UserService) service.get("userService");
        return OPUserService.getUserAdminStatus(client);
    }

    public Long getRemoteUID(SocketIOClient client) {
        @SuppressWarnings("unchecked")
        Map<String, Object> authToken = (Map<String, Object>) client.getHandshakeData().getAuthToken();
        return Long.parseLong(authToken.get("uid").toString());
    }

    public Long getRemoteGID(SocketIOClient client) {
        @SuppressWarnings("unchecked")
        Map<String, Object> authToken = (Map<String, Object>) client.getHandshakeData().getAuthToken();
        return Long.parseLong(authToken.get("gid").toString());
    }

    public String getRemoteAddress(SocketIOClient client) {
        return client.getRemoteAddress().toString().substring(1);
    }

    public Boolean validClientToken(SocketIOClient client) {
        try {
            OP_UserService OPUserService = (OP_UserService) service.get("userService");

            @SuppressWarnings("unchecked")
            Map<String, Object> authToken = (Map<String, Object>) client.getHandshakeData().getAuthToken();

            long uid = Long.parseLong(authToken.get("uid").toString());
            TokenService tokenService = (TokenService) service.get("tokenService");
            return (tokenService.validUserToken(uid, authToken.get("token").toString()) && OPUserService.checkUserLogin(uid));
        } catch (Exception e) {
            return false;
        }
    }
}

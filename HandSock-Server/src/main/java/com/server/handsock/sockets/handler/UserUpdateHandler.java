package com.server.handsock.sockets.handler;

import com.corundumstudio.socketio.AckRequest;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.console.UtilityService;
import com.server.handsock.services.ClientService;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.clients.service.ClientUserService;

import java.util.Map;

public class UserUpdateHandler {

    private final Map<String, Object> service;
    private final Map<String, Object> clientServiceList;
    private final UtilityService utilityService = new UtilityService();
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    public UserUpdateHandler(Map<String, Object> service, Map<String, Object> clientServiceList) {
        this.service = service;
        this.clientServiceList = clientServiceList;
    }

    public void handleEditUserInfo(SocketIOServer server, SocketIOClient client, Map<String, Object> data, AckRequest ackSender, String editType) {
        ClientService clientService = new ClientService(service, clientServiceList);
        if (clientService.validClientToken(client)) {
            try {
                ClientUserService clientUserService = (ClientUserService) clientServiceList.get("clientUserService");
                Map<String, Object> result = switch (editType) {
                    case "USER:NICK" ->
                            clientUserService.editForNick(clientService.getRemoteUID(client), clientService.getClientData(data, "nick"));
                    case "USER:AVATAR" ->
                            clientUserService.editForAvatar(clientService.getRemoteUID(client), clientService.getClientData(data, "path"));
                    case "USER:USERNAME" ->
                            clientUserService.editForUserName(clientService.getRemoteUID(client), clientService.getClientData(data, "username"));
                    case "USER:PASSWORD" ->
                            clientUserService.editForPassword(clientService.getRemoteUID(client), clientService.getClientData(data, "password"));
                    default -> new HandleResults().handleResultByCode(400, null, "无效的编辑类型");
                };
                ackSender.sendAckData(result);
                if (Integer.parseInt(clientService.getClientData(result, "code")) == 200) {
                    consolePrints.printInfoLogV2("User Info Edit " + result);
                    utilityService.sendGlobalMessage(server, "[RE:USER:" + editType.substring(5) + "]", result);
                }
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "请重新登录"));
    }
}

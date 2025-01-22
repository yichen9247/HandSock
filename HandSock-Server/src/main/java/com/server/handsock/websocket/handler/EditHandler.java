package com.server.handsock.websocket.handler;

import com.corundumstudio.socketio.AckRequest;
import com.server.handsock.console.MessageUtils;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.service.ClientService;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.sockets.service.OP_UserService;

import java.util.Map;

public class EditHandler {

    private final Map<String, Object> service;
    private final HandleResults handleResults = new HandleResults();

    public EditHandler(Map<String, Object> service) {
        this.service = service;
    }

    public void handleEditUserInfo(SocketIOServer server, SocketIOClient client, Map<String, Object> data, AckRequest ackSender, String editType) {
        ClientService clientService = new ClientService(service);
        if (clientService.validClientToken(client)) {
            try {
                OP_UserService OPUserService = (OP_UserService) service.get("userService");
                Map<String, Object> result = switch (editType) {
                    case "USER:NICK" ->
                            OPUserService.editForNick(clientService.getRemoteUID(client), clientService.getClientData(data, "nick"));
                    case "USER:AVATAR" ->
                            OPUserService.editForAvatar(clientService.getRemoteUID(client), clientService.getClientData(data, "path"));
                    case "USER:USERNAME" ->
                            OPUserService.editForUserName(clientService.getRemoteUID(client), clientService.getClientData(data, "username"));
                    case "USER:PASSWORD" ->
                            OPUserService.editForPassword(clientService.getRemoteUID(client), clientService.getClientData(data, "password"));
                    default -> new HandleResults().handleResultByCode(400, null, "无效的编辑类型");
                };
                ackSender.sendAckData(result);
                if (Integer.parseInt(clientService.getClientData(result, "code")) == 200) {
                    new ConsolePrints().printInfoLogV2("User Info Edit " + result);
                    new MessageUtils().sendGlobalMessage(server, "[RE:USER:" + editType.substring(5) + "]", result);
                }
            } catch (Exception e) {
                clientService.handleException(e, ackSender);
            }
        } else ackSender.sendAckData(handleResults.handleResultByCode(403, null, "请重新登录"));
    }
}

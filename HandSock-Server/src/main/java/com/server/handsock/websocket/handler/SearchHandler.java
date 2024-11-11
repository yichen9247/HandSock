package com.server.handsock.websocket.handler;

import com.corundumstudio.socketio.AckRequest;
import com.server.handsock.sockets.service.OP_ChanService;
import com.server.handsock.sockets.service.OP_ChatService;
import com.server.handsock.sockets.service.OP_UserService;
import com.server.handsock.service.ClientService;

import java.util.Map;

public class SearchHandler {

    private final Map<String, Object> service;

    public SearchHandler(Map<String, Object> service) {
        this.service = service;
    }

    public void handleSearchGroup(Map<String, Object> data, AckRequest ackRequest) {
        ClientService clientService = new ClientService(service);
        try {
            OP_ChanService OPChanService = (OP_ChanService) service.get("groupService");
            ackRequest.sendAckData(OPChanService.searchGroupByUid(Long.parseLong(clientService.getClientData(data, "gid"))));
        } catch (Exception e) {
            clientService.handleException(e, ackRequest);
        }
    }

    public void handleSearchAllGroup(AckRequest ackRequest) {
        ClientService clientService = new ClientService(service);
        try {
            OP_ChanService OPChanService = (OP_ChanService) service.get("groupService");
            ackRequest.sendAckData(OPChanService.searchAllGroup());
        } catch (Exception e) {
            clientService.handleException(e, ackRequest);
        }
    }

    public void handleSearchAllUser(Map<String, Object> data, AckRequest ackRequest) {
        ClientService clientService = new ClientService(service);
        try {
            OP_UserService OPUserService = (OP_UserService) service.get("userService");
            ackRequest.sendAckData(OPUserService.queryAllUser());
        } catch (Exception e) {
            clientService.handleException(e, ackRequest);
        }
    }

    public void handleSearchAllHistory(Map<String, Object> data, AckRequest ackRequest) {
        ClientService clientService = new ClientService(service);
        try {
            OP_ChatService OPChatService = (OP_ChatService) service.get("chatService");
            ackRequest.sendAckData(OPChatService.searchAllChatHistory(Long.parseLong(clientService.getClientData(data, "gid"))));
        } catch (Exception e) {
            clientService.handleException(e, ackRequest);
        }
    }
}

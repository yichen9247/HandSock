package com.server.handsock.sockets.handler;

import com.corundumstudio.socketio.AckRequest;
import com.server.handsock.clients.service.ClientChannelService;
import com.server.handsock.clients.service.ClientChatService;
import com.server.handsock.clients.service.ClientUserService;
import com.server.handsock.services.ClientService;

import java.util.Map;

public class SearchHandler {

    private final Map<String, Object> service;
    private final Map<String, Object> clientServiceList;

    public SearchHandler(Map<String, Object> service, Map<String, Object> clientServiceList)  {
        this.service = service;
        this.clientServiceList = clientServiceList;
    }

    public void handleSearchGroup(Map<String, Object> data, AckRequest ackRequest) {
        ClientService clientService = new ClientService(service, clientServiceList);
        try {
            ClientChannelService clientChannelService = (ClientChannelService) clientServiceList.get("clientChannelService");
            ackRequest.sendAckData(clientChannelService.searchGroupByUid(Long.parseLong(clientService.getClientData(data, "gid"))));
        } catch (Exception e) {
            clientService.handleException(e, ackRequest);
        }
    }

    public void handleSearchAllGroup(AckRequest ackRequest) {
        ClientService clientService = new ClientService(service, clientServiceList);
        try {
            ClientChannelService clientChannelService = (ClientChannelService) clientServiceList.get("clientChannelService");
            ackRequest.sendAckData(clientChannelService.searchAllGroup());
        } catch (Exception e) {
            clientService.handleException(e, ackRequest);
        }
    }

    public void handleSearchAllUser(Map<String, Object> data, AckRequest ackRequest) {
        ClientService clientService = new ClientService(service, clientServiceList);
        try {
            ClientUserService clientUserService = (ClientUserService) clientServiceList.get("clientUserService");
            ackRequest.sendAckData(clientUserService.queryAllUser());
        } catch (Exception e) {
            clientService.handleException(e, ackRequest);
        }
    }

    public void handleSearchAllHistory(Map<String, Object> data, AckRequest ackRequest) {
        ClientService clientService = new ClientService(service, clientServiceList);
        try {
            ClientChatService clientChatService = (ClientChatService) clientServiceList.get("clientChatService");
            ackRequest.sendAckData(clientChatService.searchAllChatHistory(Long.parseLong(clientService.getClientData(data, "gid"))));
        } catch (Exception e) {
            clientService.handleException(e, ackRequest);
        }
    }
}

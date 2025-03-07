package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.services.ClientService;
import com.server.handsock.sockets.handler.SearchHandler;
import com.server.handsock.utils.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SearchListener {

    private final SocketIOServer server;
    private final SearchHandler searchHandler;
    private final ClientService clientService;

    @Autowired
    public SearchListener(SearchHandler searchHandler, ClientService clientService) {
        this.searchHandler = searchHandler;
        this.clientService = clientService;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener() {
        server.addEventListener("[SEARCH:GROUP]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            searchHandler.handleSearchGroup(typedData, ackSender);
        });

        server.addEventListener("[SEARCH:USER:ALL]", Map.class, (client, data, ackSender) -> {
            searchHandler.handleSearchAllUser(ackSender);
        });

        server.addEventListener("[SEARCH:GROUP:ALL]", Map.class, (client, data, ackSender) -> {
            searchHandler.handleSearchAllGroup(ackSender);
        });

        server.addEventListener("[SEARCH:HISTORY:ALL]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            searchHandler.handleSearchAllHistory(typedData, ackSender);
            client.joinRoom(String.valueOf(clientService.getRemoteGID(client)));
        });
    }
}

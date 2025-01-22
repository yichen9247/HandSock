package com.server.handsock.websocket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.websocket.handler.SearchHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SearchListener {
    public void addEventListener(SocketIOServer server, Map<String, Object> service) {
        SearchHandler searchHandler = new SearchHandler(service);

        server.addEventListener("[SEARCH:GROUP]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            searchHandler.handleSearchGroup(typedData, ackSender);
        });

        server.addEventListener("[SEARCH:USER:ALL]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            searchHandler.handleSearchAllUser(typedData, ackSender);
        });

        server.addEventListener("[SEARCH:GROUP:ALL]", Map.class, (client, data, ackSender) -> {
            searchHandler.handleSearchAllGroup(ackSender);
        });

        server.addEventListener("[SEARCH:HISTORY:ALL]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            searchHandler.handleSearchAllHistory(typedData, ackSender);
        });
    }
}

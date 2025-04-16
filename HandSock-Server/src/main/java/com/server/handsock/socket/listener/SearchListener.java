package com.server.handsock.socket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.common.data.CommonSearchPage;
import com.server.handsock.common.data.SocketSearchChannel;
import com.server.handsock.common.utils.GlobalService;
import com.server.handsock.service.ClientService;
import com.server.handsock.socket.handler.SearchHandler;
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
        server.addEventListener("[SEARCH:GROUP]", SocketSearchChannel.class, (client, data, ackSender) -> {
            searchHandler.handleSearchGroup(data, ackSender);
        });

        server.addEventListener("[SEARCH:FORUM:POST]", CommonSearchPage.class, (client, data, ackSender) -> {
            searchHandler.handleSearchForumPost(data, ackSender);
        });

        server.addEventListener("[SEARCH:USER:ALL]", Map.class, (client, data, ackSender) -> {
            searchHandler.handleSearchAllUser(ackSender);
        });

        server.addEventListener("[SEARCH:GROUP:ALL]", Map.class, (client, data, ackSender) -> {
            searchHandler.handleSearchAllGroup(ackSender);
        });

        server.addEventListener("[SEARCH:NOTICE:ALL]", CommonSearchPage.class, (client, data, ackSender) -> {
            searchHandler.handleSearchAllNotice(data, ackSender);
        });

        server.addEventListener("[SEARCH:FORUM:COMMENT]", CommonSearchPage.class, (client, data, ackSender) -> {
            searchHandler.handleSearchPostComment(data, ackSender);
        });

        server.addEventListener("[SEARCH:HISTORY:ALL]", SocketSearchChannel.class, (client, data, ackSender) -> {
            searchHandler.handleSearchAllHistory(data, ackSender);
            client.joinRoom(String.valueOf(clientService.getRemoteGID(client)));
        });
    }
}

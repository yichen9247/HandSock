package com.server.handsock.websocket;

import com.corundumstudio.socketio.*;
import org.springframework.stereotype.Service;
import com.server.handsock.websocket.listener.*;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.service.ClientService;
import com.server.handsock.websocket.eventer.OnlineEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Service
public class SocketIOListener {

    private final Map<String, Object> service;
    private final Map<String, Object> listener;

    @Autowired
    public SocketIOListener(Map<String, Object> service, Map<String, Object> listener) {
        this.service = service;
        this.listener = listener;
    }

    public void addServerEventListener(SocketIOServer server) {
        OnlineEvent onlineEvent = new OnlineEvent();

        server.addConnectListener(client -> {
            ClientService clientService = new ClientService(service);
            onlineEvent.sendUserConnect(server, client, clientService.getRemoteAddress(client));
        });

        server.addDisconnectListener(client -> {
            ClientService clientService = new ClientService(service);
            onlineEvent.sendUserDisconnect(server, client, clientService.getRemoteAddress(client));
        });

        server.addEventListener("[ONLINE:LOGIN]", Map.class, (client, data, ackSender) -> {
            ackSender.sendAckData(Map.of("code", 200));
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            onlineEvent.sendUserOnlineLogin(server, client, typedData);
        });

        try {
            CheckListener checkListener = (CheckListener) listener.get("checkListener");
            checkListener.addEventListener(server, service);

            SendListener sendListener = (SendListener) listener.get("sendListener");
            sendListener.addEventListener(server, service);

            EditListener editListener = (EditListener) listener.get("editListener");
            editListener.addEventListener(server, service);

            AdminListener adminListener = (AdminListener) listener.get("adminListener");
            adminListener.addEventListener(server, service);

            UserListener userListener = (UserListener) listener.get("userListener");
            userListener.addEventListener(server, service, onlineEvent);

            SystemListener systemListener = (SystemListener) listener.get("systemListener");
            systemListener.addEventListener(server, service);

            ClientListener clientListener = (ClientListener) listener.get("clientListener");
            clientListener.addEventListener(server, service);

            SearchListener searchListener = (SearchListener) listener.get("searchListener");
            searchListener.addEventListener(server, service);

            ReportListener reportListener = (ReportListener) listener.get("reportListener");
            reportListener.addEventListener(server, service);
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
        }
    }
}
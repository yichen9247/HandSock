package com.server.handsock.sockets;

import com.corundumstudio.socketio.*;
import org.springframework.stereotype.Service;
import com.server.handsock.sockets.listener.*;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.services.ClientService;
import com.server.handsock.sockets.eventer.OnlineEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Service
public class SocketIOListener {

    private final Map<String, Object> service;
    private final Map<String, Object> listenerList;
    private final Map<String, Object> clientServiceList;
    private final Map<String, Object> serverServiceList;


    @Autowired
    public SocketIOListener(Map<String, Object> service, Map<String, Object> listenerList, Map<String, Object> clientServiceList, Map<String, Object> serverServiceList) {
        this.service = service;
        this.listenerList = listenerList;
        this.clientServiceList = clientServiceList;
        this.serverServiceList = serverServiceList;
    }

    public void addServerEventListener(SocketIOServer server) {
        OnlineEvent onlineEvent = new OnlineEvent();

        server.addConnectListener(thisClient -> {
            ClientService clientService = new ClientService(service, clientServiceList);
            onlineEvent.sendUserConnect(server, thisClient, clientService.getRemoteAddress(thisClient));
        });

        server.addDisconnectListener(thisClient -> {
            ClientService clientService = new ClientService(service, clientServiceList);
            onlineEvent.sendUserDisconnect(server, thisClient, clientService.getRemoteAddress(thisClient));
        });

        server.addEventListener("[ONLINE:LOGIN]", Map.class, (client, data, ackSender) -> {
            ackSender.sendAckData(Map.of("code", 200));
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            onlineEvent.sendUserOnlineLogin(server, client, typedData);
        });

        try {
            CheckListener checkListener = (CheckListener) listenerList.get("checkListener");
            checkListener.addEventListener(server, service);

            SendingListener sendingListener = (SendingListener) listenerList.get("sendingListener");
            sendingListener.addEventListener(server, service, clientServiceList, serverServiceList);

            UserUpdateListener userUpdateListener = (UserUpdateListener) listenerList.get("userUpdateListener");
            userUpdateListener.addEventListener(server, service, clientServiceList);

            AdminListener adminListener = (AdminListener) listenerList.get("adminListener");
            adminListener.addEventListener(server, service, clientServiceList, serverServiceList);

            UserAuthListener userAuthListener = (UserAuthListener) listenerList.get("userAuthListener");
            userAuthListener.addEventListener(server, onlineEvent, service, clientServiceList, serverServiceList);

            SysConfigListener sysConfigListener = (SysConfigListener) listenerList.get("sysConfigListener");
            sysConfigListener.addEventListener(server, service, clientServiceList, serverServiceList);

            ClientListener clientListener = (ClientListener) listenerList.get("clientListener");
            clientListener.addEventListener(server, service, clientServiceList);

            SearchListener searchListener = (SearchListener) listenerList.get("searchListener");
            searchListener.addEventListener(server, clientServiceList, clientServiceList);

            UserReportListener userReportListener = (UserReportListener) listenerList.get("userReportListener");
            userReportListener.addEventListener(server, service, clientServiceList);
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
        }
    }
}
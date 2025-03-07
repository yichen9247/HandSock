package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.sockets.eventer.OnlineEvent;
import com.server.handsock.sockets.handler.AdminHandler;
import com.server.handsock.utils.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AdminListener {

    private final OnlineEvent onlineEvent;
    private final SocketIOServer server;
    private final AdminHandler adminHandler;

    @Autowired
    public AdminListener(AdminHandler adminHandler, OnlineEvent onlineEvent) {
        this.onlineEvent = onlineEvent;
        this.adminHandler = adminHandler;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener()  {
        server.addEventListener("[RE:FORCE:LOAD]", Map.class, (client, data, ackSender) -> {
            adminHandler.forceReloadClient(server, client, ackSender, "[RE:FORCE:LOAD]", onlineEvent);
        });

        server.addEventListener("[RE:FORCE:CONNECT]", Map.class, (client, data, ackSender) -> {
            adminHandler.forceReloadClient(server, client, ackSender, "[RE:FORCE:CONNECT]", onlineEvent);
        });

        server.addEventListener("[RE:HISTORY:CLEAR]", Map.class, (client, data, ackSender) -> {
            adminHandler.forceReloadClient(server, client, ackSender, "[RE:HISTORY:CLEAR]", onlineEvent);
        });

        server.addEventListener("[DEL:ADMIN:USER]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "deleteUser", "uid", null);
        });

        server.addEventListener("[DEL:ADMIN:CHAT]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "deleteChat", "sid", null);
        });

        server.addEventListener("[DEL:ADMIN:REPO]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "deleteRepo", "rid", null);
        });

        server.addEventListener("[DEL:ADMIN:CHAN]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "deleteChan", "gid", null);
        });

        server.addEventListener("[DEL:ADMIN:BANNER]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "deleteBanner", "bid", null);
        });

        server.addEventListener("[DEL:ADMIN:NOTICE]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "deleteNotice", "nid", null);
        });

        server.addEventListener("[DEL:ADMIN:UPLOAD]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "deleteUpload", "fid", null);
        });

        server.addEventListener("[DEL:ADMIN:SYSTEM:LOGS]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "deleteSystemLogs", "", null);
        });

        server.addEventListener("[ADD:ADMIN:CHAN]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "createChan", "", null);
        });

        server.addEventListener("[ADD:ADMIN:BANNER]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "createBanner", "", null);
        });

        server.addEventListener("[ADD:ADMIN:NOTICE]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "createNotice", "", null);
        });

        server.addEventListener("[GET:ADMIN:DASH:DATA]", Map.class, (client, data, ackSender) -> {
            adminHandler.getDashboardData(client, ackSender);
        });

        server.addEventListener("[GET:ADMIN:USER:LIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getUserList", "", null);
        });

        server.addEventListener("[GET:ADMIN:BANNER:LIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getBannerList", "", null);
        });

        server.addEventListener("[GET:ADMIN:NOTICE:LIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getNoticeList", "", null);
        });

        server.addEventListener("[GET:ADMIN:CHAT:LIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getChatList", "", null);
        });

        server.addEventListener("[GET:ADMIN:CHAN:LIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getChanList", "", null);
        });

        server.addEventListener("[GET:ADMIN:REPO:LIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getRepoList", "", null);
        });

        server.addEventListener("[GET:ADMIN:UPLOAD:LIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getUploadList", "", null);
        });

        server.addEventListener("[GET:ADMIN:CHAT:CONTENT]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getChatContent", "sid", null);
        });

        server.addEventListener("[GET:ADMIN:SYSTEM:LOGS]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getSystemLogs", "", null);
        });

        server.addEventListener("[SET:ADMIN:CHAN:INFO]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "updateChanInfo", "", null);
        });

        server.addEventListener("[SET:ADMIN:BANNER:INFO]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "updateBannerInfo", "", null);
        });

        server.addEventListener("[SET:ADMIN:NOTICE:INFO]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "updateNoticeInfo", "", null);
        });

        server.addEventListener("[SET:ADMIN:USER:INFO]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "updateUserInfo", "", null);
        });

        server.addEventListener("[SET:ADMIN:USER:PASSWORD]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "updateUserPassword", "uid", "password");
        });

        server.addEventListener("[SET:ADMIN:CHAN:OPEN:STATUS]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "updateChanOpenStatus", "gid", "status");
        });

        server.addEventListener("[SET:ADMIN:CHAN:ACTIVE:STATUS]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "updateChanActiveStatus", "gid", "status");
        });

        server.addEventListener("[SET:ADMIN:USER:TABOO:STATUS]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "updateUserTabooStatus", "uid", "status");
        });
    }
}

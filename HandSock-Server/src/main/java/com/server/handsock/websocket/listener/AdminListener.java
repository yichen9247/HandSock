package com.server.handsock.websocket.listener;

import org.springframework.stereotype.Service;
import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.websocket.handler.AdminHandler;

import java.util.Map;

@Service
public class AdminListener {
    public void addEventListener(SocketIOServer server, Map<String, Object> service) {
        AdminHandler adminHandler = new AdminHandler(service);

        server.addEventListener("[RE:FORCE:LOAD]", Map.class, (client, data, ackSender) -> {
            adminHandler.forceReloadClient(server, client, ackSender, "[RE:FORCE:LOAD]");
        });

        server.addEventListener("[RE:FORCE:CONNECT]", Map.class, (client, data, ackSender) -> {
            adminHandler.forceReloadClient(server, client, ackSender, "[RE:FORCE:CONNECT]");
        });

        server.addEventListener("[RE:HISTORY:CLEAR]", Map.class, (client, data, ackSender) -> {
            adminHandler.forceReloadClient(server, client, ackSender, "[RE:HISTORY:CLEAR]");
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

        server.addEventListener("[DEL:ADMIN:UPLOAD]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "deleteUpload", "fid", null);
        });

        server.addEventListener("[DEL:ADMIN:SYSTEM:LOGS]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "deleteSystemLogs", null, null);
        });

        server.addEventListener("[ADD:ADMIN:CHAN]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "createChan", null, null);
        });

        server.addEventListener("[GET:ADMIN:DASH:DATA]", Map.class, (client, data, ackSender) -> {
            adminHandler.getDashboardData(client, ackSender);
        });

        server.addEventListener("[GET:ADMIN:USER:LIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getUserList", null, null);
        });

        server.addEventListener("[GET:ADMIN:CHAT:LIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getChatList", null, null);
        });

        server.addEventListener("[GET:ADMIN:CHAN:LIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getChanList", null, null);
        });

        server.addEventListener("[GET:ADMIN:REPO:LIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getRepoList", null, null);
        });

        server.addEventListener("[GET:ADMIN:UPLOAD:LIST]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getUploadList", null, null);
        });

        server.addEventListener("[GET:ADMIN:CHAT:CONTENT]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getChatContent", "sid", null);
        });

        server.addEventListener("[GET:ADMIN:SYSTEM:LOGS]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "getSystemLogs", null, null);
        });

        server.addEventListener("[SET:ADMIN:CHAN:INFO]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "updateChanInfo", null, null);
        });

        server.addEventListener("[SET:ADMIN:USER:INFO]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            adminHandler.handleAdminRequest(client, typedData, ackSender, "updateUserInfo", null, null);
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

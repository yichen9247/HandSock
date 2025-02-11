package com.server.handsock.sockets.handler;

import com.server.handsock.admin.service.*;
import com.corundumstudio.socketio.AckRequest;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.UtilityService;
import com.server.handsock.services.ClientService;
import com.server.handsock.console.HandleResults;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminHandler {

    private final Map<String, Object> service;
    private final Map<String, Object> clientServiceList;
    private final Map<String, Object> serverServiceList;
    private final UtilityService utilityService = new UtilityService();
    private final HandleResults handleResults = new HandleResults();
    private static final Logger logger = Logger.getLogger(AdminHandler.class.getName());

    public AdminHandler(Map<String, Object> service, Map<String, Object> clientServiceList, Map<String, Object> serverServiceList) {
        this.service = service;
        this.clientServiceList = clientServiceList;
        this.serverServiceList = serverServiceList;
    }

    public void handleAdminRequest(SocketIOClient client, Map<String, Object> data, AckRequest ackSender, String action, String uidKey, String valueKey) {
        if (isValidClient(client)) {
            ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
            return;
        }

        try {
            Object value = data != null ? getClientData(data, uidKey) : "none";
            Object valueData = null;
            if (data != null) valueData = valueKey != null ? getClientData(data, valueKey) : null;

            switch (action) {
                case "deleteUser":
                    sendAckData(ackSender, deleteUser(value));
                    break;
                case "deleteChan":
                    sendAckData(ackSender, deleteChan(value));
                    break;
                case "deleteChat":
                    sendAckData(ackSender, deleteChat(value));
                    break;
                case "deleteRepo":
                    sendAckData(ackSender, deleteRepo(value));
                    break;
                case "deleteUpload":
                    sendAckData(ackSender, deleteUpload(value));
                    break;
                case "deleteSystemLogs":
                    sendAckData(ackSender, deleteSystemLogs());
                    break;
                case "createChan":
                    sendAckData(ackSender, createChan(data));
                    break;
                case "updateChanInfo":
                    sendAckData(ackSender, updateChanInfo(data));
                    break;
                case "updateUserInfo":
                    sendAckData(ackSender, updateUserInfo(data));
                    break;
                case "getUserList":
                    sendAckData(ackSender, getUserList(data));
                    break;
                case "getChatList":
                    sendAckData(ackSender, getChatList(data));
                    break;
                case "getChanList":
                    sendAckData(ackSender, getChanList(data));
                    break;
                case "getRepoList":
                    sendAckData(ackSender, getRepoList(data));
                    break;
                case "getSystemLogs":
                    sendAckData(ackSender, getSystemLogs());
                    break;
                case "getUploadList":
                    sendAckData(ackSender, getUploadList(data));
                    break;
                case "getChatContent":
                    sendAckData(ackSender, getChatContent(value));
                    break;
                case "updateUserPassword":
                    if (valueData != null) sendAckData(ackSender, updateUserPassword(value, valueData));
                    break;
                case "updateChanOpenStatus":
                    if (valueData != null) sendAckData(ackSender, updateChanOpenStatus(value, valueData));
                    break;
                case "updateChanActiveStatus":
                    if (valueData != null) sendAckData(ackSender, updateChanActiveStatus(value, valueData));
                    break;
                case "updateUserTabooStatus":
                    if (valueData != null) sendAckData(ackSender, updateUserTabooStatus(value, valueData));
                    break;
                default:
                    sendAckData(ackSender, handleResults.handleResultByCode(400, null, "未知操作"));
                    break;
            }
        } catch (Exception e) {
            handleException(e, ackSender);
        }
    }

    private boolean isValidClient(SocketIOClient client) {
        ClientService clientService = new ClientService(service, clientServiceList);
        return !clientService.validClientToken(client) || !clientService.getIsAdmin(client);
    }

    private Object getClientData(Map<String, Object> data, String key) {
        return data.get(key);
    }

    private void sendAckData(AckRequest ackSender, Object data) {
        ackSender.sendAckData(data);
    }

    private void handleException(Exception e, AckRequest ackSender) {
        logger.log(Level.SEVERE, "Exception occurred", e);
        ackSender.sendAckData(handleResults.handleResultByCode(500, null, "内部服务器错误"));
    }

    private Object deleteUser(Object value) {
        ServerUserService serverUserService = (ServerUserService) serverServiceList.get("serverUserService");
        return serverUserService.deleteUser(Long.parseLong(value.toString()));
    }

    private Object deleteChan(Object value) {
        ServerChannelService serverChannelService = (ServerChannelService) serverServiceList.get("serverChannelService");
        return serverChannelService.deleteChan(Long.parseLong(value.toString()));
    }

    private Object deleteRepo(Object value) {
        ServerReportService serverReportService = (ServerReportService) serverServiceList.get("serverReportService");
        return serverReportService.deleteReport(value.toString());
    }

    private Object deleteUpload(Object value) {
        ServerUploadService serverUploadService = (ServerUploadService) serverServiceList.get("serverUploadService");
        return serverUploadService.deleteUpload(value.toString());
    }

    private Object deleteChat(Object value) {
        ServerChatService serverChatService = (ServerChatService) serverServiceList.get("serverChatService");
        return serverChatService.deleteChat(value.toString());
    }

    private Object getChatContent(Object value) {
        ServerChatService serverChatService = (ServerChatService) serverServiceList.get("serverChatService");
        return serverChatService.getChatContent(value.toString());
    }

    private Object getUserList(Map<String, Object> data) {
        ClientService clientService = new ClientService(service, clientServiceList);
        ServerUserService serverUserService = (ServerUserService) serverServiceList.get("serverUserService");
        int page = Integer.parseInt(clientService.getClientData(data, "page"));
        int limit = Integer.parseInt(clientService.getClientData(data, "limit"));
        return serverUserService.getUserList(page, limit);
    }

    private Object getChatList(Map<String, Object> data) {
        ClientService clientService = new ClientService(service, clientServiceList);
        ServerChatService serverChatService = (ServerChatService) serverServiceList.get("serverChatService");
        int page = Integer.parseInt(clientService.getClientData(data, "page"));
        int limit = Integer.parseInt(clientService.getClientData(data, "limit"));
        return serverChatService.getChatList(page, limit);
    }

    private Object getChanList(Map<String, Object> data) {
        ClientService clientService = new ClientService(service, clientServiceList);
        ServerChannelService serverChannelService = (ServerChannelService) serverServiceList.get("serverChannelService");
        int page = Integer.parseInt(clientService.getClientData(data, "page"));
        int limit = Integer.parseInt(clientService.getClientData(data, "limit"));
        return serverChannelService.getChanList(page, limit);
    }

    private Object getRepoList(Map<String, Object> data) {
        ClientService clientService = new ClientService(service, clientServiceList);
        ServerReportService serverReportService = (ServerReportService) serverServiceList.get("serverReportService");
        int page = Integer.parseInt(clientService.getClientData(data, "page"));
        int limit = Integer.parseInt(clientService.getClientData(data, "limit"));
        return serverReportService.getReportList(page, limit);
    }

    private Object getUploadList(Map<String, Object> data) {
        ClientService clientService = new ClientService(service, clientServiceList);
        ServerUploadService serverUploadService = (ServerUploadService) serverServiceList.get("serverUploadService");
        int page = Integer.parseInt(clientService.getClientData(data, "page"));
        int limit = Integer.parseInt(clientService.getClientData(data, "limit"));
        return serverUploadService.getUploadList(page, limit);
    }

    private Object updateUserPassword(Object value, Object valueData) {
        ServerUserService serverUserService = (ServerUserService) service.get("serverUserService");
        return serverUserService.updateUserPassword(Long.parseLong(value.toString()), valueData.toString());
    }

    private Object updateChanOpenStatus(Object value, Object valueData) {
        ServerChannelService serverChannelService = (ServerChannelService) serverServiceList.get("serverChannelService");
        return serverChannelService.updateChanOpenStatus(Long.parseLong(value.toString()), Integer.parseInt(valueData.toString()));
    }

    private Object updateChanActiveStatus(Object value, Object valueData) {
        ServerChannelService serverChannelService = (ServerChannelService) serverServiceList.get("serverChannelService");
        return serverChannelService.updateChanActiveStatus(Long.parseLong(value.toString()), Integer.parseInt(valueData.toString()));
    }

    private Object updateUserTabooStatus(Object value, Object valueData) {
        ServerUserService serverUserService = (ServerUserService) serverServiceList.get("serverUserService");
        return serverUserService.updateUserTabooStatus(Long.parseLong(value.toString()), valueData.toString());
    }

    private Object createChan(Map<String, Object> data) {
        ClientService clientService = new ClientService(service, clientServiceList);
        ServerChannelService serverChannelService = (ServerChannelService) serverServiceList.get("serverChannelService");
        return serverChannelService.createChan(Long.parseLong(clientService.getClientData(data, "gid")), clientService.getClientData(data, "name"), clientService.getClientData(data, "avatar"), clientService.getClientData(data, "notice"), Boolean.parseBoolean(clientService.getClientData(data, "aiRole")));
    }

    private Object updateUserInfo(Map<String, Object> data) {
        ClientService clientService = new ClientService(service, clientServiceList);
        ServerUserService serverUserService = (ServerUserService) serverServiceList.get("serverUserService");
        return serverUserService.updateUserInfo(Long.parseLong(clientService.getClientData(data, "uid")), clientService.getClientData(data, "username"), clientService.getClientData(data, "nick"), clientService.getClientData(data, "avatar"), Boolean.parseBoolean(clientService.getClientData(data, "robot")));
    }

    private Object updateChanInfo(Map<String, Object> data) {
        ClientService clientService = new ClientService(service, clientServiceList);
        ServerChannelService serverChannelService = (ServerChannelService) serverServiceList.get("serverChannelService");
        return serverChannelService.updateChan(Long.parseLong(clientService.getClientData(data, "gid")), clientService.getClientData(data, "name"), clientService.getClientData(data, "avatar"), clientService.getClientData(data, "notice"), Boolean.parseBoolean(clientService.getClientData(data, "aiRole")));
    }

    public void getDashboardData(SocketIOClient client, AckRequest ackSender) {
        ClientService clientService = new ClientService(service, clientServiceList);
        try {
            if (isValidClient(client)) {
                ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
                return;
            }
            ServerDashService serverDashService = (ServerDashService) serverServiceList.get("serverDashService");
            ackSender.sendAckData(serverDashService.getDashboardData());
        } catch (Exception e) {
            clientService.handleException(e, ackSender);
        }
    }

    private Object getSystemLogs() {
        ServerLogService serverLogService = (ServerLogService) serverServiceList.get("serverLogService");
        return serverLogService.getSystemLogs();
    }

    private Object deleteSystemLogs() {
        ServerLogService serverLogService = (ServerLogService) serverServiceList.get("serverLogService");
        return serverLogService.deleteSystemLogs();
    }

    public void forceReloadClient(SocketIOServer server, SocketIOClient client, AckRequest ackSender, String event) {
        ClientService clientService = new ClientService(service, clientServiceList);
        try {
            if (isValidClient(client)) {
                ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
                return;
            }

            if (Objects.equals(event, "[RE:HISTORY:CLEAR]")) {
                ServerChatService serverChatService = (ServerChatService) serverServiceList.get("serverChatService");
                serverChatService.clearAllChatHistory();
            }

            utilityService.sendGlobalMessage(server, event, null);
            ackSender.sendAckData(new HashMap<>(){{
                put("code", 200);
                put("message", "指令已发出");
            }});
            new ConsolePrints().printInfoLogV2("Force Client Event：" + event);
        } catch (Exception e) {
            clientService.handleException(e, ackSender);
        }
    }
}
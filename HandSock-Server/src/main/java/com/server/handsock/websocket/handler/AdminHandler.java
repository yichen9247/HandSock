package com.server.handsock.websocket.handler;

import com.server.handsock.admin.service.*;
import com.corundumstudio.socketio.AckRequest;
import com.server.handsock.console.MessageUtils;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.service.ClientService;
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
    private final HandleResults handleResults = new HandleResults();
    private static final Logger logger = Logger.getLogger(AdminHandler.class.getName());

    public AdminHandler(Map<String, Object> service) {
        this.service = service;
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
        ClientService clientService = new ClientService(service);
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
        OR_UserService orUserService = (OR_UserService) service.get("or_userService");
        return orUserService.deleteUser(Long.parseLong(value.toString()));
    }

    private Object deleteChan(Object value) {
        OR_ChanService orChanService = (OR_ChanService) service.get("or_chanService");
        return orChanService.deleteChan(Long.parseLong(value.toString()));
    }

    private Object deleteRepo(Object value) {
        OR_RepoService orRepoService = (OR_RepoService) service.get("or_repoService");
        return orRepoService.deleteReport(value.toString());
    }

    private Object deleteUpload(Object value) {
        OR_UploadService orUploadService = (OR_UploadService) service.get("or_uploadService");
        return orUploadService.deleteUpload(value.toString());
    }

    private Object deleteChat(Object value) {
        OR_ChatService orChatService = (OR_ChatService) service.get("or_chatService");
        return orChatService.deleteChat(value.toString());
    }

    private Object getChatContent(Object value) {
        OR_ChatService orChatService = (OR_ChatService) service.get("or_chatService");
        return orChatService.getChatContent(value.toString());
    }

    private Object getUserList(Map<String, Object> data) {
        ClientService clientService = new ClientService(service);
        OR_UserService orUserService = (OR_UserService) service.get("or_userService");
        int page = Integer.parseInt(clientService.getClientData(data, "page"));
        int limit = Integer.parseInt(clientService.getClientData(data, "limit"));
        return orUserService.getUserList(page, limit);
    }

    private Object getChatList(Map<String, Object> data) {
        ClientService clientService = new ClientService(service);
        OR_ChatService orChatService = (OR_ChatService) service.get("or_chatService");
        int page = Integer.parseInt(clientService.getClientData(data, "page"));
        int limit = Integer.parseInt(clientService.getClientData(data, "limit"));
        return orChatService.getChatList(page, limit);
    }

    private Object getChanList(Map<String, Object> data) {
        ClientService clientService = new ClientService(service);
        OR_ChanService orChanService = (OR_ChanService) service.get("or_chanService");
        int page = Integer.parseInt(clientService.getClientData(data, "page"));
        int limit = Integer.parseInt(clientService.getClientData(data, "limit"));
        return orChanService.getChanList(page, limit);
    }

    private Object getRepoList(Map<String, Object> data) {
        ClientService clientService = new ClientService(service);
        OR_RepoService orRepoService = (OR_RepoService) service.get("or_repoService");
        int page = Integer.parseInt(clientService.getClientData(data, "page"));
        int limit = Integer.parseInt(clientService.getClientData(data, "limit"));
        return orRepoService.getReportList(page, limit);
    }

    private Object getUploadList(Map<String, Object> data) {
        ClientService clientService = new ClientService(service);
        OR_UploadService orUploadService = (OR_UploadService) service.get("or_uploadService");
        int page = Integer.parseInt(clientService.getClientData(data, "page"));
        int limit = Integer.parseInt(clientService.getClientData(data, "limit"));
        return orUploadService.getUploadList(page, limit);
    }

    private Object updateUserPassword(Object value, Object valueData) {
        OR_UserService orUserService = (OR_UserService) service.get("or_userService");
        return orUserService.updateUserPassword(Long.parseLong(value.toString()), valueData.toString());
    }

    private Object updateChanOpenStatus(Object value, Object valueData) {
        OR_ChanService orChanService = (OR_ChanService) service.get("or_chanService");
        return orChanService.updateChanOpenStatus(Long.parseLong(value.toString()), Integer.parseInt(valueData.toString()));
    }

    private Object updateChanActiveStatus(Object value, Object valueData) {
        OR_ChanService orChanService = (OR_ChanService) service.get("or_chanService");
        return orChanService.updateChanActiveStatus(Long.parseLong(value.toString()), Integer.parseInt(valueData.toString()));
    }

    private Object updateUserTabooStatus(Object value, Object valueData) {
        OR_UserService orUserService = (OR_UserService) service.get("or_userService");
        return orUserService.updateUserTabooStatus(Long.parseLong(value.toString()), valueData.toString());
    }

    private Object createChan(Map<String, Object> data) {
        ClientService clientService = new ClientService(service);
        OR_ChanService orChanService = (OR_ChanService) service.get("or_chanService");
        return orChanService.createChan(Long.parseLong(clientService.getClientData(data, "gid")), clientService.getClientData(data, "name"), clientService.getClientData(data, "avatar"), clientService.getClientData(data, "notice"));
    }

    private Object updateUserInfo(Map<String, Object> data) {
        ClientService clientService = new ClientService(service);
        OR_UserService or_userService = (OR_UserService) service.get("or_userService");
        return or_userService.updateUserInfo(Long.parseLong(clientService.getClientData(data, "uid")), clientService.getClientData(data, "username"), clientService.getClientData(data, "nick"), clientService.getClientData(data, "avatar"), Boolean.parseBoolean(clientService.getClientData(data, "robot")));
    }

    private Object updateChanInfo(Map<String, Object> data) {
        ClientService clientService = new ClientService(service);
        OR_ChanService orChanService = (OR_ChanService) service.get("or_chanService");
        return orChanService.updateChan(Long.parseLong(clientService.getClientData(data, "gid")), clientService.getClientData(data, "name"), clientService.getClientData(data, "avatar"), clientService.getClientData(data, "notice"));
    }

    public void getDashboardData(SocketIOClient client, AckRequest ackSender) {
        ClientService clientService = new ClientService(service);
        try {
            if (isValidClient(client)) {
                ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
                return;
            }
            OR_DashService orDashService = (OR_DashService) service.get("or_dashService");
            ackSender.sendAckData(orDashService.getDashboardData());
        } catch (Exception e) {
            clientService.handleException(e, ackSender);
        }
    }

    private Object getSystemLogs() {
        OR_LogService orLogService = (OR_LogService) service.get("or_logService");
        return orLogService.getSystemLogs();
    }

    private Object deleteSystemLogs() {
        OR_LogService orLogService = (OR_LogService) service.get("or_logService");
        return orLogService.deleteSystemLogs();
    }

    public void forceReloadClient(SocketIOServer server, SocketIOClient client, AckRequest ackSender, String event) {
        ClientService clientService = new ClientService(service);
        try {
            if (isValidClient(client)) {
                ackSender.sendAckData(handleResults.handleResultByCode(403, null, "禁止访问"));
                return;
            }

            if (Objects.equals(event, "[RE:HISTORY:CLEAR]")) {
                OR_ChatService or_chatService = (OR_ChatService) service.get("or_chatService");
                or_chatService.clearAllChatHistory();
            }

            new MessageUtils().sendGlobalMessage(server, event, null);
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
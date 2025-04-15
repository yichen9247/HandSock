package com.server.handsock.socket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.admin.service.ServerDashService;
import com.server.handsock.admin.service.ServerLogService;
import com.server.handsock.common.data.AdminMannerModel;
import com.server.handsock.common.utils.GlobalService;
import com.server.handsock.socket.eventer.OnlineEvent;
import com.server.handsock.socket.handler.AdminHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AdminListener {

    private final SocketIOServer server;
    private final OnlineEvent onlineEvent;
    private final AdminHandler adminHandler;
    private final ServerLogService serverLogService;
    private final ServerDashService serverDashService;

    @Autowired
    public AdminListener(AdminHandler adminHandler, OnlineEvent onlineEvent, ServerLogService serverLogService, ServerDashService serverDashService) {
        this.onlineEvent = onlineEvent;
        this.adminHandler = adminHandler;
        this.serverLogService = serverLogService;
        this.serverDashService = serverDashService;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener()  {
        server.addEventListener("[GET:ADMIN:SYSTEM:LOGS]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, serverLogService::getSystemLogs);
        });

        server.addEventListener("[GET:ADMIN:DASH:DATA]", Map.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, serverDashService::getDashboardData);
        });

        server.addEventListener("[DEL:ADMIN:SYSTEM:LOGS]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, serverLogService::deleteSystemLogs);
        });

        server.addEventListener("[RE:FORCE:LOAD]", Map.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.forceReloadClient(server, "[RE:FORCE:LOAD]", onlineEvent));
        });

        server.addEventListener("[RE:FORCE:CONNECT]", Map.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.forceReloadClient(server, "[RE:FORCE:CONNECT]", onlineEvent));
        });

        server.addEventListener("[RE:HISTORY:CLEAR]", Map.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.forceReloadClient(server, "[RE:HISTORY:CLEAR]", onlineEvent));
        });

        server.addEventListener("[GET:ADMIN:USER:LIST]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.getUserList(data));
        });

        server.addEventListener("[DEL:ADMIN:USER]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.deleteUser(data));
        });

        server.addEventListener("[DEL:ADMIN:CHAT]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.deleteChat(data));
        });

        server.addEventListener("[DEL:ADMIN:REPO]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.deleteRepo(data));
        });

        server.addEventListener("[DEL:ADMIN:POST]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.deletePost(data));
        });

        server.addEventListener("[DEL:ADMIN:COMMENT]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.deleteComment(data));
        });

        server.addEventListener("[DEL:ADMIN:CHAN]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.deleteChan(data));
        });

        server.addEventListener("[DEL:ADMIN:BANNER]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.deleteBanner(data));
        });

        server.addEventListener("[DEL:ADMIN:NOTICE]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.deleteNotice(data));
        });

        server.addEventListener("[DEL:ADMIN:UPLOAD]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.deleteUpload(data));
        });

        server.addEventListener("[ADD:ADMIN:CHAN]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.createChan(data));
        });

        server.addEventListener("[ADD:ADMIN:BANNER]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.createBanner(data));
        });

        server.addEventListener("[ADD:ADMIN:NOTICE]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.createNotice(data));
        });

        server.addEventListener("[GET:ADMIN:BANNER:LIST]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.getBannerList(data));
        });

        server.addEventListener("[GET:ADMIN:NOTICE:LIST]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.getNoticeList(data));
        });

        server.addEventListener("[GET:ADMIN:CHAT:LIST]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.getChatList(data));
        });

        server.addEventListener("[GET:ADMIN:CHAN:LIST]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.getChanList(data));
        });

        server.addEventListener("[GET:ADMIN:REPO:LIST]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.getRepoList(data));
        });

        server.addEventListener("[GET:ADMIN:POST:LIST]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.getPostList(data));
        });

        server.addEventListener("[GET:ADMIN:COMMENT:LIST]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.getCommentList(data));
        });

        server.addEventListener("[GET:ADMIN:UPLOAD:LIST]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.getUploadList(data));
        });

        server.addEventListener("[GET:ADMIN:CHAT:CONTENT]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.getChatContent(data));
        });

        server.addEventListener("[SET:ADMIN:CHAN:INFO]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.updateChanInfo(data));
        });

        server.addEventListener("[SET:ADMIN:BANNER:INFO]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.updateBannerInfo(data));
        });

        server.addEventListener("[SET:ADMIN:NOTICE:INFO]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.updateNoticeInfo(data));
        });

        server.addEventListener("[SET:ADMIN:USER:INFO]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.updateUserInfo(data));
        });

        server.addEventListener("[SET:ADMIN:USER:PASSWORD]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.updateUserPassword(data));
        });

        server.addEventListener("[SET:ADMIN:CHAN:OPEN:STATUS]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.updateChanOpenStatus(data));
        });

        server.addEventListener("[SET:ADMIN:CHAN:ACTIVE:STATUS]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.updateChanActiveStatus(data));
        });

        server.addEventListener("[SET:ADMIN:USER:STATUS]", AdminMannerModel.class, (client, data, ackSender) -> {
            adminHandler.handleAdminRequest(client, ackSender, () -> adminHandler.updateUserStatus(data));
        });
    }
}

package com.server.handsock.configs;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.admin.service.*;
import com.server.handsock.service.*;
import com.server.handsock.sockets.service.OP_ChanService;
import com.server.handsock.sockets.service.OP_ChatService;
import com.server.handsock.sockets.service.OP_UserService;
import com.server.handsock.api.service.OU_SystemService;
import com.corundumstudio.socketio.Configuration;
import com.server.handsock.websocket.SocketIOListener;
import com.server.handsock.websocket.listener.*;
import com.server.handsock.websocket.eventer.RobotsEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

    private final EmailService emailService;
    private final RobotsEvent robotsEvent;
    private final OP_ChatService OPChatService;
    private final OP_UserService OPUserService;
    private final CacheService cacheService;
    private final TokenService tokenService;
    private final OP_ChanService OPChanService;
    private final SocketService socketService;
    private final OU_SystemService OUSystemService;
    private final HistoryService historyService;
    private final OR_UserService orUserService;
    private final OR_ChatService orChatService;
    private final OR_ChanService orChanService;
    private final OR_DashService orDashService;
    private final OR_UploadService orUploadService;
    private final UserListener userListener;
    private final SendListener sendListener;
    private final EditListener editListener;
    private final AdminListener adminListener;
    private final SystemListener systemListener;
    private final ClientListener clientListener;
    private final SearchListener searchListener;

    @Autowired
    public SocketIOConfig(
            CacheService cacheService, TokenService tokenService, SocketService socketService, HistoryService historyService, EmailService emailService, RobotsEvent robotsEvent,
            OP_ChatService OPChatService, OP_UserService OPUserService, OP_ChanService OPChanService, OU_SystemService OUSystemService,
            OR_UserService orUserService, OR_ChatService orChatService, OR_ChanService orChanService, OR_DashService orDashService, OR_UploadService orUploadService,
            UserListener userListener, SendListener sendListener, EditListener editListener, AdminListener adminListener, SystemListener systemListener, ClientListener clientListener, SearchListener searchListener
    ) {
        this.robotsEvent = robotsEvent;
        this.cacheService = cacheService;
        this.OPChatService = OPChatService;
        this.OPUserService = OPUserService;
        this.tokenService = tokenService;
        this.OPChanService = OPChanService;
        this.socketService = socketService;
        this.OUSystemService = OUSystemService;
        this.historyService = historyService;

        this.emailService = emailService;
        this.orUserService = orUserService;
        this.orChatService = orChatService;
        this.orChanService = orChanService;
        this.orDashService = orDashService;
        this.orUploadService = orUploadService;

        this.userListener = userListener;
        this.sendListener = sendListener;
        this.editListener = editListener;
        this.adminListener = adminListener;
        this.systemListener = systemListener;
        this.clientListener = clientListener;
        this.searchListener = searchListener;
    }

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setPort(socketService.getPort());
        config.setOrigin(socketService.getOrigin());
        config.setHostname(socketService.getHost());
        config.setPingTimeout(socketService.getPingTimeout());
        config.setPingInterval(socketService.getPingInterval());
        config.setUpgradeTimeout(socketService.getUpgradeTimeout());
        SocketIOServer socketIOServer = new SocketIOServer(config);

        Map<String, Object> services = new HashMap<>();
        services.put("emailService", emailService);
        services.put("robotsEvent", robotsEvent);
        services.put("userService", OPUserService);
        services.put("chatService", OPChatService);
        services.put("cacheService", cacheService);
        services.put("tokenService", tokenService);
        services.put("groupService", OPChanService);
        services.put("socketService", socketService);
        services.put("systemService", OUSystemService);
        services.put("historyService", historyService);
        services.put("or_userService", orUserService);
        services.put("or_chatService", orChatService);
        services.put("or_chanService", orChanService);
        services.put("or_dashService", orDashService);
        services.put("or_uploadService", orUploadService);

        Map<String, Object> listeners = new HashMap<>();
        listeners.put("sendListener", sendListener);
        listeners.put("editListener", editListener);
        listeners.put("userListener", userListener);
        listeners.put("adminListener", adminListener);
        listeners.put("systemListener", systemListener);
        listeners.put("clientListener", clientListener);
        listeners.put("searchListener", searchListener);
        new SocketIOListener(services, listeners).addServerEventListener(socketIOServer);
        return socketIOServer;
    }
}
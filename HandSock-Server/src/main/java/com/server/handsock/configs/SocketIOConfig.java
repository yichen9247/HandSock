package com.server.handsock.configs;

import com.server.handsock.service.*;
import com.server.handsock.admin.service.*;
import com.server.handsock.websocket.listener.*;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import com.server.handsock.websocket.SocketIOListener;
import com.server.handsock.api.service.OU_SystemService;
import com.server.handsock.websocket.eventer.RobotsEvent;
import com.server.handsock.sockets.service.OP_ChanService;
import com.server.handsock.sockets.service.OP_ChatService;
import com.server.handsock.sockets.service.OP_RepoService;
import com.server.handsock.sockets.service.OP_UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

    private final CacheService cacheService;
    private final TokenService tokenService;
    private final SocketService socketService;
    private final HistoryService historyService;
    private final EmailService emailService;
    private final RobotsEvent robotsEvent;
    private final OP_ChatService OPChatService;
    private final OP_UserService OPUserService;
    private final OP_ChanService OPChanService;
    private final OP_RepoService OPRepoService;
    private final OU_SystemService OUSystemService;
    private final OR_LogService orLogService;
    private final OR_UserService orUserService;
    private final OR_ChatService orChatService;
    private final OR_ChanService orChanService;
    private final OR_DashService orDashService;
    private final OR_RepoService orRepoService;
    private final OR_UploadService orUploadService;
    private final UserListener userListener;
    private final SendListener sendListener;
    private final EditListener editListener;
    private final CheckListener checkListener;
    private final AdminListener adminListener;
    private final SystemListener systemListener;
    private final ClientListener clientListener;
    private final SearchListener searchListener;
    private final ReportListener reportListener;

    @Autowired
    public SocketIOConfig(
            @Lazy CacheService cacheService, @Lazy TokenService tokenService, @Lazy SocketService socketService, @Lazy HistoryService historyService,
            @Lazy EmailService emailService, @Lazy RobotsEvent robotsEvent,
            @Lazy OP_ChatService OPChatService, @Lazy OP_UserService OPUserService, @Lazy OP_ChanService OPChanService, @Lazy OP_RepoService OPRepoService, @Lazy OU_SystemService OUSystemService,
            @Lazy OR_UserService orUserService, @Lazy OR_ChatService orChatService, @Lazy OR_ChanService orChanService, @Lazy OR_DashService orDashService, @Lazy OR_UploadService orUploadService, @Lazy OR_RepoService orRepoService, @Lazy OR_LogService orLogService,
            @Lazy UserListener userListener, @Lazy SendListener sendListener, @Lazy EditListener editListener, @Lazy AdminListener adminListener, @Lazy SystemListener systemListener, @Lazy ClientListener clientListener, @Lazy SearchListener searchListener, @Lazy ReportListener reportListener, @Lazy CheckListener checkListener
    ) {
        this.cacheService = cacheService;
        this.tokenService = tokenService;
        this.socketService = socketService;
        this.historyService = historyService;
        this.emailService = emailService;
        this.robotsEvent = robotsEvent;
        this.OPChatService = OPChatService;
        this.OPUserService = OPUserService;
        this.OPChanService = OPChanService;
        this.OPRepoService = OPRepoService;
        this.OUSystemService = OUSystemService;
        this.orLogService = orLogService;
        this.orUserService = orUserService;
        this.orChatService = orChatService;
        this.orChanService = orChanService;
        this.orDashService = orDashService;
        this.orRepoService = orRepoService;
        this.orUploadService = orUploadService;
        this.userListener = userListener;
        this.sendListener = sendListener;
        this.editListener = editListener;
        this.adminListener = adminListener;
        this.checkListener = checkListener;
        this.systemListener = systemListener;
        this.clientListener = clientListener;
        this.searchListener = searchListener;
        this.reportListener = reportListener;
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

        Map<String, Object> services = createServicesMap();
        Map<String, Object> listeners = createListenersMap();

        new SocketIOListener(services, listeners).addServerEventListener(socketIOServer);
        return socketIOServer;
    }

    private Map<String, Object> createServicesMap() {
        Map<String, Object> services = new HashMap<>();
        services.put("emailService", emailService);
        services.put("robotsEvent", robotsEvent);
        services.put("userService", OPUserService);
        services.put("chatService", OPChatService);
        services.put("repoService", OPRepoService);
        services.put("cacheService", cacheService);
        services.put("tokenService", tokenService);
        services.put("groupService", OPChanService);
        services.put("socketService", socketService);
        services.put("systemService", OUSystemService);
        services.put("historyService", historyService);
        services.put("or_logService", orLogService);
        services.put("or_userService", orUserService);
        services.put("or_chatService", orChatService);
        services.put("or_chanService", orChanService);
        services.put("or_dashService", orDashService);
        services.put("or_repoService", orRepoService);
        services.put("or_uploadService", orUploadService);
        return services;
    }

    private Map<String, Object> createListenersMap() {
        Map<String, Object> listeners = new HashMap<>();
        listeners.put("sendListener", sendListener);
        listeners.put("editListener", editListener);
        listeners.put("userListener", userListener);
        listeners.put("adminListener", adminListener);
        listeners.put("checkListener", checkListener);
        listeners.put("systemListener", systemListener);
        listeners.put("clientListener", clientListener);
        listeners.put("searchListener", searchListener);
        listeners.put("reportListener", reportListener);
        return listeners;
    }
}

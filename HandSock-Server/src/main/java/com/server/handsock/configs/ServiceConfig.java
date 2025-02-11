package com.server.handsock.configs;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.admin.service.*;
import com.server.handsock.api.service.UploadService;
import com.server.handsock.console.AppProperties;
import com.server.handsock.services.CacheService;
import com.server.handsock.services.HistoryService;
import com.server.handsock.services.TokenService;
import com.server.handsock.clients.service.ClientChannelService;
import com.server.handsock.clients.service.ClientChatService;
import com.server.handsock.clients.service.ClientReportService;
import com.server.handsock.clients.service.ClientUserService;
import com.server.handsock.sockets.SocketIOListener;
import com.server.handsock.sockets.eventer.RobotsEvent;
import com.server.handsock.sockets.listener.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;
import java.util.Map;

@org.springframework.context.annotation.Configuration
public class ServiceConfig {

    // listeners
    private final CheckListener checkListener;
    private final AdminListener adminListener;
    private final ClientListener clientListener;
    private final SearchListener searchListener;
    private final SendingListener sendingListener;
    private final UserAuthListener userAuthListener;
    private final SysConfigListener sysConfigListener;
    private final UserUpdateListener userUpdateListener;
    private final UserReportListener userReportListener;

    // Client Services
    private final ClientChatService clientChatService;
    private final ClientUserService clientUserService;
    private final ClientReportService clientReportService;
    private final ClientChannelService clientChannelService;

    // Server Services
    private final ServerLogService serverLogService;
    private final ServerUserService serverUserService;
    private final ServerChatService serverChatService;
    private final ServerDashService serverDashService;
    private final ServerSystemService serverSystemService;
    private final ServerReportService serverReportService;
    private final ServerUploadService serverUploadService;
    private final ServerChannelService serverChannelService;

    // Other Services
    private final RobotsEvent robotsEvent;
    private final CacheService cacheService;
    private final TokenService tokenService;
    private final UploadService uploadService;
    private final AppProperties appProperties;
    private final HistoryService historyService;

    @Autowired
    public ServiceConfig(
            @Lazy AppProperties appProperties,
            @Lazy RobotsEvent robotsEvent, @Lazy UploadService uploadService,
            @Lazy CacheService cacheService, @Lazy TokenService tokenService, @Lazy HistoryService historyService,
            @Lazy ClientChatService clientChatService, @Lazy ClientUserService clientUserService, @Lazy ClientChannelService clientChannelService, @Lazy ClientReportService clientReportService,
            @Lazy UserAuthListener userAuthListener, @Lazy SendingListener sendingListener, @Lazy UserUpdateListener userUpdateListener, @Lazy AdminListener adminListener, @Lazy SysConfigListener sysConfigListener, @Lazy ClientListener clientListener, @Lazy SearchListener searchListener, @Lazy UserReportListener userReportListener, @Lazy CheckListener checkListener,
            @Lazy ServerUserService serverUserService, @Lazy ServerChatService serverChatService, @Lazy ServerChannelService serverChannelService, @Lazy ServerDashService serverDashService, @Lazy ServerUploadService serverUploadService, @Lazy ServerReportService serverReportService, @Lazy ServerLogService serverLogService, @Lazy ServerSystemService serverSystemService
    ) {
        // listeners
        this.adminListener = adminListener;
        this.checkListener = checkListener;
        this.clientListener = clientListener;
        this.searchListener = searchListener;
        this.sendingListener = sendingListener;
        this.userAuthListener = userAuthListener;
        this.sysConfigListener = sysConfigListener;
        this.userUpdateListener = userUpdateListener;
        this.userReportListener = userReportListener;

        // Client Services
        this.clientChatService = clientChatService;
        this.clientUserService = clientUserService;
        this.clientReportService = clientReportService;
        this.clientChannelService = clientChannelService;

        // Server Services
        this.serverLogService = serverLogService;
        this.serverChatService = serverChatService;
        this.serverUserService = serverUserService;
        this.serverDashService = serverDashService;
        this.serverReportService = serverReportService;
        this.serverSystemService = serverSystemService;
        this.serverUploadService = serverUploadService;
        this.serverChannelService = serverChannelService;

        // Other Services
        this.robotsEvent = robotsEvent;
        this.cacheService = cacheService;
        this.tokenService = tokenService;
        this.appProperties = appProperties;
        this.uploadService = uploadService;
        this.historyService = historyService;
    }

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setPort(appProperties.getPort());
        config.setOrigin(appProperties.getOrigin());
        config.setHostname(appProperties.getHost());
        config.setPingTimeout(appProperties.getPingTimeout());
        config.setPingInterval(appProperties.getPingInterval());
        config.setUpgradeTimeout(appProperties.getUpgradeTimeout());
        SocketIOServer socketIOServer = new SocketIOServer(config);

        Map<String, Object> services = createServicesMap();
        Map<String, Object> listenerList = createListenerMap();
        Map<String, Object> clientServiceList = createClientServiceMap();
        Map<String, Object> serverServiceList = createServerServiceMap();
        new SocketIOListener(services, listenerList, clientServiceList, serverServiceList).addServerEventListener(socketIOServer);
        return socketIOServer;
    }

    private Map<String, Object> createServicesMap() {
        Map<String, Object> services = new HashMap<>();
        services.put("robotsEvent", robotsEvent);
        services.put("cacheService", cacheService);
        services.put("tokenService", tokenService);
        services.put("uploadService", uploadService);
        services.put("historyService", historyService);
        return services;
    }

    private Map<String, Object> createClientServiceMap() {
        Map<String, Object> clientServiceList = new HashMap<>();
        clientServiceList.put("clientChatService", clientChatService);
        clientServiceList.put("clientUserService", clientUserService);
        clientServiceList.put("clientReportService", clientReportService);
        clientServiceList.put("clientChannelService", clientChannelService);
        return clientServiceList;
    }

    private Map<String, Object> createServerServiceMap() {
        Map<String, Object> serverServiceList = new HashMap<>();
        serverServiceList.put("serverLogService", serverLogService);
        serverServiceList.put("serverChatService", serverChatService);
        serverServiceList.put("serverUserService", serverUserService);
        serverServiceList.put("serverDashService", serverDashService);
        serverServiceList.put("serverSystemService", serverSystemService);
        serverServiceList.put("serverReportService", serverReportService);
        serverServiceList.put("serverUploadService", serverUploadService);
        serverServiceList.put("serverChannelService", serverChannelService);
        return serverServiceList;
    }

    private Map<String, Object> createListenerMap() {
        Map<String, Object> listenerList = new HashMap<>();
        listenerList.put("adminListener", adminListener);
        listenerList.put("checkListener", checkListener);
        listenerList.put("clientListener", clientListener);
        listenerList.put("searchListener", searchListener);
        listenerList.put("sendingListener", sendingListener);
        listenerList.put("userAuthListener", userAuthListener);
        listenerList.put("sysConfigListener", sysConfigListener);
        listenerList.put("userReportListener", userReportListener);
        listenerList.put("userUpdateListener", userUpdateListener);
        return listenerList;
    }
}

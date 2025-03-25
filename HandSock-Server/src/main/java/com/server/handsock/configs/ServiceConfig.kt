package com.server.handsock.configs

import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.store.RedissonStoreFactory
import com.server.handsock.props.HandProp
import com.server.handsock.sockets.SocketIOListener
import com.server.handsock.sockets.eventer.OnlineEvent
import com.server.handsock.sockets.listener.*
import com.server.handsock.utils.GlobalService
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

@Configuration
open class ServiceConfig
@Autowired constructor(
    private val onlineEvent: OnlineEvent,
    private val checkListener: CheckListener,
    private val handProp: HandProp,
    @param:Lazy private val adminListener: AdminListener,
    @param:Lazy private val clientListener: ClientListener,
    @param:Lazy private val searchListener: SearchListener,
    @param:Lazy private val sendingListener: SendingListener,
    @param:Lazy private val userAuthListener: UserAuthListener,
    @param:Lazy private val sysConfigListener: SysConfigListener,
    @param:Lazy private val userUpdateListener: UserUpdateListener,
    @param:Lazy private val userReportListener: UserReportListener
) {
    private val config = com.corundumstudio.socketio.Configuration()
    @Bean
    open fun socketIOServer(redissonClient: RedissonClient?): SocketIOServer {
        config.port = handProp.port
        config.origin = handProp.origin
        config.hostname = handProp.host
        config.pingTimeout = handProp.pingTimeout
        config.pingInterval = handProp.pingInterval
        config.upgradeTimeout = handProp.upgradeTimeout
        config.storeFactory = RedissonStoreFactory(redissonClient)
        val socketIOServer = SocketIOServer(config)
        GlobalService.socketIOServer = socketIOServer

        SocketIOListener(
            onlineEvent = onlineEvent,
            adminListener = adminListener, checkListener = checkListener, clientListener = clientListener,
            searchListener = searchListener, sendingListener = sendingListener, sysConfigListener = sysConfigListener,
            userUpdateListener = userUpdateListener, userReportListener = userReportListener, userAuthListener = userAuthListener
        ).addServerEventListener(socketIOServer)
        return socketIOServer
    }
}

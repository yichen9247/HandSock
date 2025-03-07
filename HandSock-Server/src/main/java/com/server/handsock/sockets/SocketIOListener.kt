package com.server.handsock.sockets

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.server.handsock.sockets.eventer.OnlineEvent
import com.server.handsock.sockets.listener.*
import com.server.handsock.utils.ConsoleUtils
import com.server.handsock.utils.HandUtils

class SocketIOListener(
    private val onlineEvent: OnlineEvent,
    private val adminListener: AdminListener,
    private val checkListener: CheckListener,
    private val clientListener: ClientListener,
    private val searchListener: SearchListener,
    private val sendingListener: SendingListener,
    private val userAuthListener: UserAuthListener,
    private val sysConfigListener: SysConfigListener,
    private val userReportListener: UserReportListener,
    private val userUpdateListener: UserUpdateListener
) {
    fun addServerEventListener(server: SocketIOServer) {
        server.addConnectListener {
            onlineEvent.sendUserConnect(
                client = it,
                server = server
            )
        }

        server.addDisconnectListener {
            onlineEvent.sendUserDisconnect(
                client = it,
                server = server
            )
        }

        server.addEventListener("[ONLINE:LOGIN]", Map::class.java) { client: SocketIOClient?, data: Map<*, *>?, ackSender: AckRequest ->
            ackSender.sendAckData(java.util.Map.of("code", 200))
            @Suppress("UNCHECKED_CAST")
            val typedData = data as Map<String, Any>?
            onlineEvent.sendUserOnlineLogin(server, client!!, typedData!!)
        }

        try {
            adminListener.addEventListener()
            clientListener.addEventListener()
            searchListener.addEventListener()
            userAuthListener.addEventListener()
            sysConfigListener.addEventListener()
            userUpdateListener.addEventListener()
            userReportListener.addEventListener()
            checkListener.addEventListener(HandUtils)
            sendingListener.addEventListener(HandUtils)
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
        }
    }
}
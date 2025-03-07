package com.server.handsock.sockets.eventer

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.transport.NamespaceClient
import com.server.handsock.admin.service.ServerSystemService
import com.server.handsock.clients.service.ClientChannelService
import com.server.handsock.clients.service.ClientChatService
import com.server.handsock.clients.service.ClientUserService
import com.server.handsock.services.AuthService
import com.server.handsock.services.CacheService
import com.server.handsock.services.ClientService
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RobotSender @Autowired constructor(
    private val robotEvent: RobotEvent,
    private val authService: AuthService,
    private val clientService: ClientService,
    private val clientUserService: ClientUserService,
    private val clientChatService: ClientChatService,
    private val clientChannelService: ClientChannelService,
    private val cacheService: CacheService,
    private val serverSystemService: ServerSystemService
) {
    fun handleSendMessageOnBot(dataEvent: Any, server: SocketIOServer, client: NamespaceClient, ackRequest: AckRequest) {
        if (isValidMessage(client)) return
        try {
            @Suppress("UNCHECKED_CAST")
            val data = dataEvent as Map<String?, Any>
            val content = clientService.getClientData(data, CONTENT_KEY)

            @Suppress("UNCHECKED_CAST")
            val authToken = client.handshakeData.authToken as Map<String?, Any>

            val robotReturn = robotEvent.handleRobotCommand(client, content)
            if (robotReturn != null) {
                Thread.sleep(800)
                val robotResult = clientChatService.insertChatMessage(clientService.getClientData(data, TYPE_KEY), clientUserService.robotInnerStatus!!, clientService.getClientData(authToken, "gid").toLong(), "none", robotReturn)
                @Suppress("UNCHECKED_CAST")
                sendGlobalMessageIfSuccess(robotResult as Map<String?, Any>, server, client)
            }
        } catch (e: Exception) {
            ackRequest.sendAckData(HandUtils.printErrorLog(e))
        } finally {
            cacheService.writeRedisMessageCache(clientService.getRemoteUID(client))
        }
    }

    private fun isSuccessResult(result: Map<String?, Any>): Boolean {
        return clientService.getClientData(result, CODE_KEY).toInt() == 200
    }

    private fun sendGlobalMessageIfSuccess(result: Map<String?, Any>, server: SocketIOServer, client: SocketIOClient) {
        if (isSuccessResult(result)) HandUtils.sendRoomMessage(
            client = client,
            server = server,
            event = "[MESSAGE]",
            content = result["data"]
        )
    }

    private fun isValidMessage(client: SocketIOClient): Boolean {
        return !cacheService.validRedisMessageCache(clientService.getRemoteUID(client))
                || clientUserService.robotInnerStatus == null
                || !clientUserService.getUserInnerStatus(clientService.getRemoteUID(client))
                || !clientChannelService.getChanOpenStatus(clientService.getRemoteGID(client))
                || serverSystemService.getSystemKeyStatus("taboo")
                && !clientService.getIsAdmin(client)
                || clientUserService.getUserTabooStatus(clientService.getRemoteUID(client))
                && !clientService.getIsAdmin(client) || !authService.validClientTokenBySocket(client)
    }

    companion object {
        private const val TYPE_KEY = "type"
        private const val CODE_KEY = "code"
        private const val CONTENT_KEY = "content"
    }
}

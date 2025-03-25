package com.server.handsock.sockets.eventer

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.transport.NamespaceClient
import com.server.handsock.admin.service.ServerSystemService
import com.server.handsock.clients.service.ClientChannelService
import com.server.handsock.clients.service.ClientChatService
import com.server.handsock.clients.service.ClientUserService
import com.server.handsock.services.AuthService
import com.server.handsock.services.CacheService
import com.server.handsock.services.ClientService
import com.server.handsock.utils.HandUtils
import com.server.handsock.utils.RobotUtils
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
    fun handleSendMessageOnBot(dataEvent: Any, client: NamespaceClient, ackRequest: AckRequest) {
        if (isValidMessage(client)) return
        try {
            @Suppress("UNCHECKED_CAST")
            val data = dataEvent as Map<String?, Any>
            val content = clientService.getClientData(data, "content")

            @Suppress("UNCHECKED_CAST")
            val authToken = client.handshakeData.authToken as Map<String?, Any>
            val robotReturn = robotEvent.handleRobotCommand(client, content)
            if (robotReturn != null) {
                Thread.sleep(800)
                RobotUtils.sendRobotMessage(
                    address = "none",
                    content = robotReturn,
                    clientService = clientService,
                    clientChatService = clientChatService,
                    clientUserService = clientUserService,
                    gid = clientService.getClientData(authToken, "gid").toLong()
                )
            }
        } catch (e: Exception) {
            ackRequest.sendAckData(HandUtils.printErrorLog(e))
        } finally {
            cacheService.writeRedisMessageCache(clientService.getRemoteUID(client))
        }
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
}

package com.server.handsock.sockets.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.clients.service.ClientUserService
import com.server.handsock.services.AuthService
import com.server.handsock.services.ClientService
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClientHandler @Autowired constructor(
    private val authService: AuthService,
    private val clientService: ClientService,
    private val clientUserService: ClientUserService
) {
    fun handleClientInit(client: SocketIOClient, ackRequest: AckRequest) {
        authService.validClientStatusBySocket(client) {
            val result: MutableMap<String, Any> = HashMap()
            result["userinfo"] = clientUserService.queryUserInfo(clientService.getRemoteUID(client))
            ackRequest.sendAckData(HandUtils.handleResultByCode(200, result, "获取成功"))
        }
    }
}

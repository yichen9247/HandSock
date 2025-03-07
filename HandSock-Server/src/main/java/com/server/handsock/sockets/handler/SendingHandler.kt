package com.server.handsock.sockets.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.server.handsock.clients.service.ClientChatService
import com.server.handsock.services.AuthService
import com.server.handsock.services.ClientService
import com.server.handsock.utils.HandUtils
import lombok.Getter
import lombok.Setter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Getter @Setter @Service
class SendingHandler @Autowired constructor(
    private val authService: AuthService,
    private val clientService: ClientService,
    private val clientChatService: ClientChatService,
) {
    fun handleSendMessage(server: SocketIOServer, client: SocketIOClient, data: Map<String?, Any>, ackRequest: AckRequest) {
        authService.validChatMessageStatusBySocket(
            data = data,
            client = client,
            ackRequest = ackRequest,
            call = {
                try {
                    val content = HandUtils.stripHtmlTagsForString(clientService.getClientData(data, CONTENT_KEY))
                    val result = clientChatService.insertChatMessage(
                        clientService.getClientData(data, TYPE_KEY),
                        clientService.getRemoteUID(client),
                        clientService.getRemoteGID(client),
                        clientService.getRemoteAddress(client),
                        content
                    )
                    @Suppress("UNCHECKED_CAST")
                    sendAckData(ackRequest, result as Map<String?, Any>)
                    if (isSuccessResult(result)) HandUtils.sendRoomMessage(
                        client = client,
                        server = server,
                        event = "[MESSAGE]",
                        content = result["data"]
                    )
                } catch (e: Exception) {
                    ackRequest.sendAckData(HandUtils.printErrorLog(e))
                }
            }
        )
    }

    private fun isSuccessResult(result: Map<String?, Any>): Boolean {
        return clientService.getClientData(result, CODE_KEY).toInt() == 200
    }

    private fun sendAckData(ackRequest: AckRequest, result: Map<String?, Any>) {
        ackRequest.sendAckData(result)
    }

    companion object {
        private const val TYPE_KEY = "type"
        private const val CODE_KEY = "code"
        private const val CONTENT_KEY = "content"
    }
}
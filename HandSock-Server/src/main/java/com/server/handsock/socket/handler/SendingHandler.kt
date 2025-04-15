package com.server.handsock.socket.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.server.handsock.client.service.ClientChatService
import com.server.handsock.common.data.SocketUserMessage
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.service.AuthService
import com.server.handsock.service.ClientService
import com.server.handsock.socket.eventer.RobotSender
import lombok.Getter
import lombok.Setter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Getter @Setter @Service
class SendingHandler @Autowired constructor(
    private val authService: AuthService,
    private val robotSender: RobotSender,
    private val clientService: ClientService,
    private val clientChatService: ClientChatService
) {
    fun handleSendMessage(server: SocketIOServer, client: SocketIOClient, data: SocketUserMessage, ackRequest: AckRequest) {
        if (data.type == null || data.content == null)
            return ackRequest.sendAckData(HandUtils.printParamError())

        authService.validChatMessageStatusBySocket(
            data = data,
            client = client,
            ackRequest = ackRequest,
            call = {
                try {
                    val stripContent = HandUtils.stripHtmlTagsForString(data.content)
                    val result = clientChatService.insertChatMessage(
                        type = data.type,
                        content = stripContent,
                        uid = clientService.getRemoteUID(client),
                        gid = clientService.getRemoteGID(client),
                        address = clientService.getRemoteAddress(client),
                    )
                    @Suppress("UNCHECKED_CAST")
                    sendAckData(ackRequest, result as Map<String?, Any>)
                    if (isSuccessResult(result)) HandUtils.sendGlobalMessage(
                        server = server,
                        event = "[MESSAGE]",
                        content = result["data"]
                    )
                    robotSender.handleSendMessageOnBot(
                        client = client,
                        content = data.content,
                        ackRequest = ackRequest
                    )
                } catch (e: Exception) {
                    ackRequest.sendAckData(HandUtils.printErrorLog(e))
                }
            }
        )
    }

    private fun isSuccessResult(result: Map<String?, Any>): Boolean {
        return clientService.getClientData(result, "code").toInt() == 200
    }

    private fun sendAckData(ackRequest: AckRequest, result: Map<String?, Any>) {
        ackRequest.sendAckData(result)
    }
}
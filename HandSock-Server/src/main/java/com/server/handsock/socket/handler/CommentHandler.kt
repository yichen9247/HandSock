package com.server.handsock.socket.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.client.service.ClientCommentService
import com.server.handsock.common.data.SocketAddPostComment
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.service.AuthService
import com.server.handsock.service.ClientService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommentHandler @Autowired constructor(
    private val authService: AuthService,
    private val clientService: ClientService,
    private val clientCommentService: ClientCommentService
) {
    fun handleAddPostComment(client: SocketIOClient, data: SocketAddPostComment, ackSender: AckRequest) {
        ackSender.sendAckData(authService.validClientStatusBySocket(client) {
            if (data.pid == null || data.content.isNullOrEmpty() || data.content.trim().isEmpty())
                HandUtils.printParamError()
            else clientCommentService.addPostComment(
                pid = data.pid,
                parent = data.parent,
                content = data.content,
                uid = clientService.getRemoteUID(client)
            )
        })
    }
}
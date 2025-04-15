package com.server.handsock.socket.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.client.service.ClientForumService
import com.server.handsock.common.data.SocketAddForumPost
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.service.AuthService
import com.server.handsock.service.ClientService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ForumHandler @Autowired constructor(
    private val authService: AuthService,
    private val clientService: ClientService,
    private val clientForumService: ClientForumService
) {
    fun handleAddForumPost(client: SocketIOClient, data: SocketAddForumPost, ackSender: AckRequest) {
        ackSender.sendAckData(authService.validClientStatusBySocket(client) {
            if (data.title.isNullOrEmpty() || data.content.isNullOrEmpty() || data.image.isNullOrEmpty())
                HandUtils.printParamError()
            else clientForumService.addForumPost(
                title = data.title,
                image = data.image,
                content = data.content,
                uid = clientService.getRemoteUID(client)
            )
        })
    }
}
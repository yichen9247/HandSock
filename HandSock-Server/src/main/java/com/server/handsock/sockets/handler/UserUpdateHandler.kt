package com.server.handsock.sockets.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.clients.service.ClientUserService
import com.server.handsock.services.AuthService
import com.server.handsock.services.ClientService
import com.server.handsock.utils.ConsoleUtils
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserUpdateHandler @Autowired constructor(
    private val authService: AuthService,
    private val clientService: ClientService,
    private val clientUserService: ClientUserService
) {
    fun handleEditUserInfo(client: SocketIOClient, data: Map<String?, Any>, ackSender: AckRequest, editType: String?) {
        ackSender.sendAckData(authService.validClientStatusBySocket(client) {
            try {
                when (editType) {
                    "USER:NICK" -> clientUserService.editForNick(
                        clientService.getRemoteUID(client), clientService.getClientData(data, "nick")
                    )
                    "USER:AVATAR" -> clientUserService.editForAvatar(
                        clientService.getRemoteUID(client),
                        clientService.getClientData(data, "path")
                    )
                    "USER:USERNAME" -> clientUserService.editForUserName(
                        clientService.getRemoteUID(client),
                        clientService.getClientData(data, "username")
                    )
                    "USER:PASSWORD" -> clientUserService.editForPassword(
                        clientService.getRemoteUID(client),
                        clientService.getClientData(data, "password")
                    )
                    else -> HandUtils.handleResultByCode(400, null, "无效的编辑类型")
                }
            } catch (e: Exception) {
                HandUtils.printErrorLog(e)
            }
        })
    }
}

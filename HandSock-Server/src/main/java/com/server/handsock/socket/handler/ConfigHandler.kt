package com.server.handsock.socket.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.admin.service.ServerSystemService
import com.server.handsock.common.data.SocketSystemConfig
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ConfigHandler @Autowired constructor(
    private val authService: AuthService,
    private val serverSystemService: ServerSystemService
) {
    fun handleSetSystemTaboo(client: SocketIOClient, data: SocketSystemConfig, ackSender: AckRequest) {
        ackSender.sendAckData(authService.validAdminStatusBySocket(client) {
            if (data.value.isNullOrEmpty())
                HandUtils.printParamError()
            else serverSystemService.setSystemTabooStatus(data.value)
        })
    }

    fun handleSetSystemUpload(client: SocketIOClient, data: SocketSystemConfig, ackSender: AckRequest) {
        ackSender.sendAckData(authService.validAdminStatusBySocket(client) {
            if (data.value.isNullOrEmpty())
                HandUtils.printParamError()
            else serverSystemService.setSystemUploadStatus(data.value)
        })
    }

    fun handleSetSystemRegister(client: SocketIOClient, data: SocketSystemConfig, ackSender: AckRequest) {
        ackSender.sendAckData(authService.validAdminStatusBySocket(client) {
            if (data.value.isNullOrEmpty())
                HandUtils.printParamError()
            else serverSystemService.setSystemRegisterStatus(data.value)
        })
    }

    fun handleSetSystemConfigValue(client: SocketIOClient, data: SocketSystemConfig, ackSender: AckRequest) {
        ackSender.sendAckData(authService.validAdminStatusBySocket(client) {
            if (data.value .isNullOrEmpty() || data.name.isNullOrEmpty())
                HandUtils.printParamError()
            else serverSystemService.setSystemConfigValue(
                name = data.name,
                value = data.value
            )
        })
    }

    fun handleGetAllSystemConfig(client: SocketIOClient, ackSender: AckRequest) {
        ackSender.sendAckData(authService.validAdminStatusBySocket(client) {
            serverSystemService.getAllSystemConfig()
        })
    }
}

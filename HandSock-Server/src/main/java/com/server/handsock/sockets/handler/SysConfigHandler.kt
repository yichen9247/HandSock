package com.server.handsock.sockets.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.admin.service.ServerSystemService
import com.server.handsock.services.AuthService
import com.server.handsock.services.ClientService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SysConfigHandler @Autowired constructor(
    private val authService: AuthService,
    private val clientService: ClientService,
    private val serverSystemService: ServerSystemService
) {
    fun handleSetSystemTaboo(client: SocketIOClient, data: Map<String?, Any>, ackSender: AckRequest) {
        authService.validAdminStatusBySocket(client) {
            val value = clientService.getClientData(data, "value")
            ackSender.sendAckData(serverSystemService.setSystemTabooStatus(value))
        }
    }

    fun handleSetSystemUpload(client: SocketIOClient, data: Map<String?, Any>, ackSender: AckRequest) {
        authService.validAdminStatusBySocket(client) {
            val value = clientService.getClientData(data, "value")
            ackSender.sendAckData(serverSystemService.setSystemUploadStatus(value))
        }
    }

    fun handleSetSystemRegister(client: SocketIOClient, data: Map<String?, Any>, ackSender: AckRequest) {
        authService.validAdminStatusBySocket(client) {
            val value = clientService.getClientData(data, "value")
            ackSender.sendAckData(serverSystemService.setSystemRegisterStatus(value))
        }
    }

    fun handleSetSystemConfigValue(client: SocketIOClient, data: Map<String?, Any>, ackSender: AckRequest) {
        authService.validAdminStatusBySocket(client) {
            val name = clientService.getClientData(data, "name")
            val value = clientService.getClientData(data, "value")
            ackSender.sendAckData(serverSystemService.setSystemConfigValue(
                name = name,
                value = value
            ))
        }
    }

    fun handleGetSystemPlaylist(client: SocketIOClient, ackSender: AckRequest) {
        authService.validAdminStatusBySocket(client) {
            ackSender.sendAckData(serverSystemService.getSystemKeyConfig("playlist"))
        }
    }

    fun handleGetAllSystemConfig(client: SocketIOClient, ackSender: AckRequest) {
        authService.validAdminStatusBySocket(client) {
            ackSender.sendAckData(serverSystemService.allSystemConfig)
        }
    }
}

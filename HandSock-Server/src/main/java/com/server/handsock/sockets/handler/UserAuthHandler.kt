package com.server.handsock.sockets.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.server.handsock.admin.service.ServerSystemService
import com.server.handsock.clients.service.ClientUserService
import com.server.handsock.services.AuthService
import com.server.handsock.services.ClientService
import com.server.handsock.services.TokenService
import com.server.handsock.sockets.eventer.OnlineEvent
import com.server.handsock.utils.ConsoleUtils
import com.server.handsock.utils.HandUtils
import com.server.handsock.utils.IDGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserAuthHandler @Autowired constructor(
    private val authService: AuthService,
    private val onlineEvent: OnlineEvent,
    private val tokenService: TokenService,
    private val clientService: ClientService,
    private val clientUserService: ClientUserService,
    private val serverSystemService: ServerSystemService
) {
    fun handleUserLogin(client: SocketIOClient, data: Map<String?, Any>, ackSender: AckRequest) {
        try {
            if (!onlineEvent.checkClient(HandUtils.encodeStringToMD5(client.sessionId.toString()))) {
                ackSender.sendAckData(HandUtils.handleResultByCode(403, null, "禁止访问"))
            } else {
                ackSender.sendAckData(
                    clientUserService.loginUser(
                        address = clientService.getRemoteAddress(client),
                        username = clientService.getClientData(data, "username"),
                        password = clientService.getClientData(data, "password")
                    )
                )
            }
        } catch (e: Exception) {
            ackSender.sendAckData(HandUtils.printErrorLog(e))
        }
    }

    fun handleUserLogout(client: SocketIOClient, server: SocketIOServer?, ackSender: AckRequest) {
        ackSender.sendAckData(authService.validClientStatusBySocket(client) {
            if (!onlineEvent.checkClient(HandUtils.encodeStringToMD5(client.sessionId.toString()))) {
                HandUtils.handleResultByCode(403, null, "禁止访问")
            } else {
                onlineEvent.sendUserDisconnect(server!!, client)
                ConsoleUtils.printInfoLog(
                    "User Logout ${clientService.getRemoteAddress(client)} ${clientService.getRemoteUID(client)}"
                )
            }
        })
    }

    fun handleUserRegister(client: SocketIOClient, data: Map<String?, Any>, ackSender: AckRequest) {
        try {
            if (!onlineEvent.checkClient(HandUtils.encodeStringToMD5(client.sessionId.toString()))) {
                ackSender.sendAckData(HandUtils.handleResultByCode(403, null, "禁止访问"))
            } else {
                if (serverSystemService.getSystemKeyStatus("register")) {
                    val result = clientUserService.registerUser(
                        clientService.getClientData(data, "username"),
                        clientService.getClientData(data, "password"),
                        clientService.getRemoteAddress(client)
                    )
                    ackSender.sendAckData(result)
                } else ackSender.sendAckData(HandUtils.handleResultByCode(402, null, "当前禁止注册"))
            }
        } catch (e: Exception) {
            ackSender.sendAckData(HandUtils.printErrorLog(e))
        }
    }

    fun handleUserScanLogin(client: SocketIOClient, ackSender: AckRequest) {
        try {
            if (!onlineEvent.checkClient(HandUtils.encodeStringToMD5(client.sessionId.toString()))) {
                ackSender.sendAckData(HandUtils.handleResultByCode(403, null, "禁止访问"))
            } else {
                val qid = IDGenerator.generateUniqueId()
                val content = mapOf(
                    "qid" to qid,
                    "type" to "login",
                    "platform" to "H5"
                )
                tokenService.setScanStatus(qid, 0)
                /*QrcodeUtils.generateQrcode(content.toString())*/
                ackSender.sendAckData(HandUtils.handleResultByCode(200, content, "获取成功"))
            }
        } catch (e: Exception) {
            ackSender.sendAckData(HandUtils.printErrorLog(e))
        }
    }

    fun handleGetScanLoginStatus(client: SocketIOClient, data: Map<String?, Any>, ackSender: AckRequest) {
        try {
            if (!onlineEvent.checkClient(HandUtils.encodeStringToMD5(client.sessionId.toString()))) {
                ackSender.sendAckData(HandUtils.handleResultByCode(403, null, "禁止访问"))
            } else {
                val qid = clientService.getClientData(data, "qid")
                ackSender.sendAckData(clientUserService.getUserQrcodeScanStatus(
                    qid = qid,
                    address = clientService.getRemoteAddress(client)
                ))
            }
        } catch (e: Exception) {
            ackSender.sendAckData(HandUtils.printErrorLog(e))
        }
    }
}
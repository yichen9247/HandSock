package com.server.handsock.sockets.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.clients.service.ClientReportService
import com.server.handsock.services.AuthService
import com.server.handsock.services.ClientService
import org.springframework.stereotype.Service

@Service
class UserReportHandler(
    private val authService: AuthService,
    private val clientService: ClientService,
    private val clientReportService: ClientReportService
) {

    fun handleReport(client: SocketIOClient, data: Map<String?, Any>, ackSender: AckRequest) {
        ackSender.sendAckData(authService.validClientStatusBySocket(client) {
            clientReportService.addReport(
                clientService.getClientData(data, "sid"),
                clientService.getRemoteUID(client).toString().toLong(),
                clientService.getClientData(data, "reported_id").toLong(),
                clientService.getClientData(data, "reason")
            )
        })
    }
}

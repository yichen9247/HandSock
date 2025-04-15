package com.server.handsock.socket.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.client.service.ClientReportService
import com.server.handsock.common.data.SocketUserReport
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.service.AuthService
import com.server.handsock.service.ClientService
import org.springframework.stereotype.Service

@Service
class ReportHandler(
    private val authService: AuthService,
    private val clientService: ClientService,
    private val clientReportService: ClientReportService
) {
    fun handleReport(client: SocketIOClient, data: SocketUserReport, ackSender: AckRequest) {
        ackSender.sendAckData(authService.validClientStatusBySocket(client) {
            if (data.sid.isNullOrEmpty() || data.reason.isNullOrEmpty() || data.reportedId == null || data.reason.trim().isEmpty())
                HandUtils.printParamError()
            else clientReportService.addReport(
                sid = data.sid,
                reason = data.reason,
                reportedId = data.reportedId,
                reporterId = clientService.getRemoteUID(client)
            )
        })
    }
}

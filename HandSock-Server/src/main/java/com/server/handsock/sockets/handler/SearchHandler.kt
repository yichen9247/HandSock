package com.server.handsock.sockets.handler

import com.corundumstudio.socketio.AckRequest
import com.server.handsock.clients.service.ClientChannelService
import com.server.handsock.clients.service.ClientChatService
import com.server.handsock.clients.service.ClientUserService
import com.server.handsock.services.ClientService
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SearchHandler @Autowired constructor(
    private val clientService: ClientService,
    private val clientChatService: ClientChatService,
    private val clientChannelService: ClientChannelService,
    private val clientUserService: ClientUserService
) {
    fun handleSearchGroup(data: Map<String?, Any>, ackRequest: AckRequest) {
        try {
            ackRequest.sendAckData(
                clientChannelService.searchGroupByGid(
                    clientService.getClientData(data, "gid").toLong()
                )
            )
        } catch (e: Exception) {
            ackRequest.sendAckData(HandUtils.printErrorLog(e))
        }
    }

    fun handleSearchAllGroup(ackRequest: AckRequest) {
        try {
            ackRequest.sendAckData(clientChannelService.searchAllGroup())
        } catch (e: Exception) {
            ackRequest.sendAckData(HandUtils.printErrorLog(e))
        }
    }

    fun handleSearchAllUser(ackRequest: AckRequest) {
        try {
            ackRequest.sendAckData(clientUserService.queryAllUser())
        } catch (e: Exception) {
            ackRequest.sendAckData(HandUtils.printErrorLog(e))
        }
    }

    fun handleSearchAllHistory(data: Map<String?, Any>, ackRequest: AckRequest) {
        try {
            ackRequest.sendAckData(
                clientChatService.searchAllChatHistory(
                    clientService.getClientData(data, "gid").toLong()
                )
            )
        } catch (e: Exception) {
            ackRequest.sendAckData(HandUtils.printErrorLog(e))
        }
    }
}

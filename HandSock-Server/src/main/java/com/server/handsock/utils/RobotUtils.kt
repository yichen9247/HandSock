package com.server.handsock.utils

import com.server.handsock.clients.service.ClientChatService
import com.server.handsock.clients.service.ClientUserService
import com.server.handsock.services.ClientService

object RobotUtils {
    fun sendRobotMessage(
        gid: Long,
        content: String,
        address: String,
        clientService: ClientService,
        clientUserService: ClientUserService,
        clientChatService: ClientChatService
    ): Map<String, Any> {
        val robotUser = clientUserService.robotInnerStatus ?: return HandUtils.handleResultByCode(500, null, "未查找到机器人账号")
        if (robotUser.taboo == "open") return HandUtils.handleResultByCode(500, null, "机器人账号已被禁言")
        val sendResult = clientChatService.insertChatMessage(type = "text", robotUser.uid, gid, address, content)
        @Suppress("UNCHECKED_CAST")
        if (clientService.getClientData(sendResult as Map<String?, Any>, "code").toInt() == 200) {
            HandUtils.sendRoomMessageApi(
                gid = gid.toString(),
                event = "[MESSAGE]",
                content = sendResult["data"],
                server = GlobalService.socketIOServer!!
            )
        }
        return sendResult
    }
}
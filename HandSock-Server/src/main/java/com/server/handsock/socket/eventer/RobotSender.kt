package com.server.handsock.socket.eventer

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.admin.service.ServerSystemService
import com.server.handsock.client.dao.ClientUserDao
import com.server.handsock.client.mod.ClientUserModel
import com.server.handsock.client.service.ClientChannelService
import com.server.handsock.client.service.ClientChatService
import com.server.handsock.client.service.ClientUserService
import com.server.handsock.common.types.UserAuthType
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.common.utils.RobotUtils
import com.server.handsock.service.AuthService
import com.server.handsock.service.CacheService
import com.server.handsock.service.ClientService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RobotSender @Autowired constructor(
    private val robotEvent: RobotEvent,
    private val authService: AuthService,
    private val cacheService: CacheService,
    private val clientService: ClientService,
    private val clientUserDao: ClientUserDao,
    private val clientUserService: ClientUserService,
    private val clientChatService: ClientChatService,
    private val serverSystemService: ServerSystemService,
    private val clientChannelService: ClientChannelService
) {
    fun handleSendMessageOnBot(content: String, client: SocketIOClient, ackRequest: AckRequest) {
        if (isValidMessage(client)) return
        try {
            @Suppress("UNCHECKED_CAST")
            val authToken = client.handshakeData.authToken as Map<String?, Any>
            val robotReturn = robotEvent.handleRobotCommand(client, content)
            if (robotReturn != null) {
                Thread.sleep(800)
                RobotUtils.sendRobotMessage(
                    address = "none",
                    content = robotReturn,
                    clientService = clientService,
                    clientChatService = clientChatService,
                    clientUserService = clientUserService,
                    gid = clientService.getClientData(authToken, "gid").toLong()
                )
            }
        } catch (e: Exception) {
            ackRequest.sendAckData(HandUtils.printErrorLog(e))
        } finally {
            cacheService.writeRedisMessageCache(clientService.getRemoteUID(client))
        }
    }

    private fun isValidMessage(client: SocketIOClient): Boolean {
        val uid = clientService.getRemoteUID(client)
        val permission = authService.getUserAuthInfoBySocket(client)

        return !cacheService.validRedisMessageCache(clientService.getRemoteUID(client)) || clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("uid", uid)) == null || !clientChannelService.getChanOpenStatus(clientService.getRemoteGID(client)) || (serverSystemService.getSystemKeyStatus("taboo")
                && permission != UserAuthType.ADMIN_AUTHENTICATION) || (authService.getUserStatusInfoBySocket(client) == UserAuthType.TABOO_STATUS
                && permission != UserAuthType.ADMIN_AUTHENTICATION) || !authService.validClientTokenBySocket(client)
    }
}

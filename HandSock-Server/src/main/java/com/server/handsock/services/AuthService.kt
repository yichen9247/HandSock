package com.server.handsock.services

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.admin.service.ServerSystemService
import com.server.handsock.clients.service.ClientChannelService
import com.server.handsock.clients.service.ClientUserService
import com.server.handsock.utils.HandUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuthService @Autowired constructor(
    private val tokenService: TokenService,
    private val cacheService: CacheService,
    private val clientService: ClientService,
    private val clientUserService: ClientUserService,
    private val serverSystemService: ServerSystemService,
    private val clientChannelService: ClientChannelService
) {
    fun validClientTokenBySocket(client: SocketIOClient): Boolean {
        try {
            @Suppress("UNCHECKED_CAST")
            val authToken = client.handshakeData.authToken as Map<String, Any>
            val uid = authToken["uid"].toString().toLong()
            return (tokenService.validUserToken(uid, authToken["token"].toString()) && clientUserService.checkUserLogin(uid))
        } catch (e: Exception) {
            return false
        }
    }

    fun validClientTokenByRequest(request: HttpServletRequest): Boolean {
        val token = request.getHeader("token") ?: ""
        val uid = (if (request.getHeader("uid") != null) request.getHeader("uid") else "0").toLong()
        return tokenService.validUserToken(uid, token) && clientUserService.checkUserLogin(uid)
    }

    fun validClientStatusByRequest(request: HttpServletRequest, call: () -> Any): Any {
        return if (validClientTokenByRequest(request)) {
            call()
        } else HandUtils.handleResultByCode(403, null, "非法访问")
    }

    fun validAdminStatusByRequest(request: HttpServletRequest, call: () -> Any): Any {
        val uid = (if (request.getHeader("uid") != null) request.getHeader("uid") else "0").toLong()
        return if (clientUserService.getUserAdminStatusByUid(uid) && validClientTokenByRequest(request)) {
            call()
        } else HandUtils.handleResultByCode(403, null, "非法访问")
    }

    fun validClientStatusBySocket(client: SocketIOClient, call: () -> Any): Any {
        return if (validClientTokenBySocket(client)) {
            call()
        } else HandUtils.handleResultByCode(403, null, "非法访问")
    }

    fun validAdminStatusBySocket(client: SocketIOClient, call: () -> Any): Any {
        return if (clientUserService.queryUserInfo(clientService.getRemoteUID(client)).isAdmin == 1 && validClientTokenBySocket(client)) {
            call()
        } else HandUtils.handleResultByCode(403, null, "非法访问")
    }

    fun validChatMessageStatusBySocket(client: SocketIOClient, ackRequest: AckRequest, call: () -> Any, data: Map<String?, Any>) {
        if (!validClientTokenBySocket(client)) {
            ackRequest.sendAckData(ackRequest, HandUtils.handleResultByCode(403, null, "登录状态已失效"))
            return
        }
        if (!cacheService.validRedisMessageCache(clientService.getRemoteUID(client))) {
            ackRequest.sendAckData(ackRequest, HandUtils.handleResultByCode(402, null, "操作频率过快"))
            return
        }
        if (!clientUserService.getUserInnerStatus(clientService.getRemoteUID(client))) {
            ackRequest.sendAckData(ackRequest, HandUtils.handleResultByCode(402, null, "未查询到用户"))
            return
        }
        if (!clientChannelService.getChanOpenStatus(clientService.getRemoteGID(client))) {
            ackRequest.sendAckData(ackRequest, HandUtils.handleResultByCode(402, null, "该频道暂未开启"))
            return
        }
        if (serverSystemService.getSystemKeyStatus("taboo") && !clientService.getIsAdmin(client)) {
            ackRequest.sendAckData(ackRequest, HandUtils.handleResultByCode(402, null, "全频禁言开启中"))
            return
        }
        if (clientUserService.getUserTabooStatus(clientService.getRemoteUID(client)) && !clientService.getIsAdmin(client)) {
            ackRequest.sendAckData(ackRequest, HandUtils.handleResultByCode(402, null, "你正在被禁言中"))
            return
        }
        if (clientService.getClientData(data, "content").trim { it <= ' ' }.isEmpty()) {
            ackRequest.sendAckData(ackRequest, HandUtils.handleResultByCode(402, null, "发送内容不能为空"))
            return
        }
        call()
    }
}

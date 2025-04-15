package com.server.handsock.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.admin.service.ServerSystemService
import com.server.handsock.client.dao.ClientUserDao
import com.server.handsock.client.mod.ClientUserModel
import com.server.handsock.client.service.ClientChannelService
import com.server.handsock.client.service.ClientUserService
import com.server.handsock.common.data.SocketUserMessage
import com.server.handsock.common.props.HandProp
import com.server.handsock.common.types.UserAuthType
import com.server.handsock.common.utils.HandUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuthService @Autowired constructor(
    private val handProp: HandProp,
    private val tokenService: TokenService,
    private val cacheService: CacheService,
    private val clientService: ClientService,
    private val clientUserDao: ClientUserDao,
    private val clientUserService: ClientUserService,
    private val serverSystemService: ServerSystemService,
    private val clientChannelService: ClientChannelService
) {
    fun getUserAuthInfoBySocket(client: SocketIOClient): Int {
        return clientUserService.queryUserInfo(clientService.getRemoteUID(client)).permission
    }

    private fun getUserAuthInfoByRequest(request: HttpServletRequest): Int {
        val uid = (if (request.getHeader("uid") != null) request.getHeader("uid") else "0").toLong()
        return clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("uid", uid)).permission
    }

    fun getUserStatusInfoBySocket(client: SocketIOClient): Int {
        return clientUserService.queryUserInfo(clientService.getRemoteUID(client)).status
    }

    fun getUserStatusInfoByRequest(request: HttpServletRequest): Int {
        val uid = (if (request.getHeader("uid") != null) request.getHeader("uid") else "0").toLong()
        return clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("uid", uid)).status
    }

    fun validClientTokenBySocket(client: SocketIOClient): Boolean {
        try {
            @Suppress("UNCHECKED_CAST")
            val authToken = client.handshakeData.authToken as Map<String, Any>
            val uid = authToken["uid"].toString().toLong()
            return (tokenService.validUserToken(uid, authToken["token"].toString()) && clientUserDao.selectOne(
                QueryWrapper<ClientUserModel>().eq("uid", uid)) != null)
        } catch (e: Exception) {
            return false
        }
    }

    fun validClientTokenByRequest(request: HttpServletRequest): Boolean {
        try {
            val token = request.getHeader("token") ?: ""
            val uid = (if (request.getHeader("uid") != null) request.getHeader("uid") else "0").toLong()
            return tokenService.validUserToken(uid, token) && clientUserDao.selectOne(
                QueryWrapper<ClientUserModel>().eq("uid", uid)) != null
        } catch (e: Exception) {
            return false
        }
    }

    fun validClientStatusByRequest(request: HttpServletRequest, call: () -> Any): Any {
        return if (validClientTokenByRequest(request)) {
            call()
        } else HandUtils.handleResultByCode(403, null, "非法访问")
    }

    fun validClientStatusBySocket(client: SocketIOClient, call: () -> Any): Any {
        return if (validClientTokenBySocket(client)) {
            call()
        } else HandUtils.handleResultByCode(403, null, "非法访问")
    }

    fun validAdminStatusByRequest(request: HttpServletRequest, call: () -> Any): Any {
        return if (getUserAuthInfoByRequest(request) == UserAuthType.ADMIN_AUTHENTICATION && validClientTokenByRequest(request)) {
            call()
        } else HandUtils.handleResultByCode(403, null, "非法访问")
    }

    fun validAdminStatusBySocket(client: SocketIOClient, call: () -> Any): Any {
        return if (getUserAuthInfoBySocket(client) == UserAuthType.ADMIN_AUTHENTICATION && validClientTokenBySocket(client)) {
            call()
        } else HandUtils.handleResultByCode(403, null, "非法访问")
    }

    fun validOpenApiRequestLimit(type: String, request: HttpServletRequest, call: () -> Any): Any {
        val address = request.remoteAddr
        return if (tokenService.getOpenApiCache(type, address)) {
            tokenService.setOpenApiCache(type, address)
            val authorization = request.getHeader("Authorization") ?: null
            if (authorization == null || authorization != handProp.openapi) {
                HandUtils.handleResultByCode(403, null, "无权调用此接口")
            } else call()
        } else HandUtils.handleResultByCode(400, null, "请求频繁")
    }

    fun validChatMessageStatusBySocket(client: SocketIOClient, ackRequest: AckRequest, call: () -> Any, data: SocketUserMessage) {
        val uid = clientService.getRemoteUID(client)
        val status = getUserStatusInfoBySocket(client)
        val permission = getUserAuthInfoBySocket(client)

        if (!validClientTokenBySocket(client)) {
            ackRequest.sendAckData(HandUtils.handleResultByCode(403, null, "登录状态失效"))
            return
        }
        if (!cacheService.validRedisMessageCache(uid)) {
            ackRequest.sendAckData(HandUtils.handleResultByCode(402, null, "操作频率过快"))
            return
        }
        if (clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("uid", uid)) == null) {
            ackRequest.sendAckData(HandUtils.handleResultByCode(402, null, "未查询到用户"))
            return
        }
        if (!clientChannelService.getChanOpenStatus(clientService.getRemoteGID(client))) {
            ackRequest.sendAckData(HandUtils.handleResultByCode(402, null, "该频道未开启"))
            return
        }
        if (serverSystemService.getSystemKeyStatus("taboo") && permission != UserAuthType.ADMIN_AUTHENTICATION) {
            ackRequest.sendAckData(HandUtils.handleResultByCode(402, null, "全频禁言开启中"))
            return
        }
        if (status == UserAuthType.TABOO_STATUS && permission != UserAuthType.ADMIN_AUTHENTICATION) {
            ackRequest.sendAckData(HandUtils.handleResultByCode(402, null, "你正在被禁言中"))
            return
        }
        if (data.content == null || data.content.trim().isEmpty()) {
            ackRequest.sendAckData(HandUtils.handleResultByCode(402, null, "发送内容不能为空"))
            return
        }
        call()
    }
}

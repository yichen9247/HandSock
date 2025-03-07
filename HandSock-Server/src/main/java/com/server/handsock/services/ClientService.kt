package com.server.handsock.services

import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.clients.service.ClientUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClientService @Autowired constructor(
    private val clientUserService: ClientUserService
) {
    // 获取客户端数据
    fun getClientData(data: Map<String?, Any>, key: String?): String {
        return data[key].toString()
    }

    // 判断用户是否是管理员
    fun getIsAdmin(client: SocketIOClient): Boolean {
        return clientUserService.queryUserInfo(getRemoteUID(client)).isAdmin == 1
    }

    // 判断用户是否是拥有AI能力相关权限
    fun hasAiAuthorization(client: SocketIOClient): Boolean {
        return clientUserService.queryUserInfo(getRemoteUID(client)).aiAuth == 1
    }

    // 获取用户uid
    fun getRemoteUID(client: SocketIOClient): Long {
        @Suppress("UNCHECKED_CAST")
        val authToken = client.handshakeData.authToken as Map<String, Any>
        return authToken["uid"].toString().toLong()
    }

    // 获取频道gid
    fun getRemoteGID(client: SocketIOClient): Long {
        @Suppress("UNCHECKED_CAST")
        val authToken = client.handshakeData.authToken as Map<String, Any>
        return authToken["gid"].toString().toLong()
    }

    // 获取客户端IP地址
    fun getRemoteAddress(client: SocketIOClient): String {
        return client.remoteAddress.toString().substring(1)
    }
}

package com.server.handsock.socket.eventer

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.server.handsock.common.utils.ConsoleUtils
import com.server.handsock.common.utils.HandUtils
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.Volatile

@Service
class OnlineEvent {
    private val clientSet: MutableSet<String> = ConcurrentHashMap.newKeySet()
    private val userMap = ConcurrentHashMap<Long, UserInfo>()

    private fun getClientUUID(client: SocketIOClient): String {
        return HandUtils.encodeStringToMD5(client.sessionId.toString())
    }

    fun sendUserConnect(server: SocketIOServer, client: SocketIOClient) {
        val uuid = getClientUUID(client)
        if (clientSet.add(uuid)) {
            client.sendEvent("[TOKENS]", Collections.singletonMap("data", uuid))
            sendMessage(server)
        }
    }

    fun sendUserDisconnect(server: SocketIOServer, client: SocketIOClient) {
        val uuid = getClientUUID(client)
        if (clientSet.remove(uuid)) {
            processUserDisconnection(client, uuid)
            sendMessage(server)
        }
    }

    private fun processUserDisconnection(client: SocketIOClient, uuid: String) {
        try {
            val authData = client.handshakeData.authToken as? Map<*, *> ?: return
            val uidObj = authData["uid"] ?: return
            val uid = uidObj.toString().toLong()
            userMap.computeIfPresent(uid) { _, userInfo ->
                if (userInfo.removeUuid(uuid)) null else userInfo
            }
        } catch (e: NumberFormatException) {
            ConsoleUtils.printErrorLog(e)
        }
    }

    fun sendUserOnlineLogin(server: SocketIOServer, client: SocketIOClient, data: Map<String, Any>) {
        try {
            val uid = extractDataField(data, "uid").toLong()
            val status = extractDataField(data, "status").toInt()
            val platform = Objects.toString(data["platform"], "")
            if (status == 0) {
                handleUserLogout(uid, client)
            } else handleUserLogin(uid, client, platform)
            sendMessage(server)
        } catch (e: IllegalArgumentException) {
            ConsoleUtils.printErrorLog(e)
        }
    }

    private fun extractDataField(data: Map<String, Any>, field: String): String {
        val value = data[field] ?: throw IllegalArgumentException("Missing required field: $field")
        return value.toString()
    }

    private fun handleUserLogout(uid: Long, client: SocketIOClient) {
        userMap.computeIfPresent(uid) { _: Long?, userInfo: UserInfo ->
            userInfo.removeUuid(getClientUUID(client))
            if (userInfo.uuids.isEmpty()) null else userInfo
        }
    }

    private fun handleUserLogin(uid: Long, client: SocketIOClient, platform: String) {
        val uuid = getClientUUID(client)
        userMap.compute(uid) { _: Long?, existingUser: UserInfo? ->
            if (existingUser == null) {
                return@compute UserInfo(uid, uuid, platform)
            } else {
                existingUser.addUuid(uuid)
                existingUser.login = true
                existingUser.platform = platform
                return@compute existingUser
            }
        }
    }

    fun clearClient() {
        userMap.clear()
        clientSet.clear()
    }

    fun checkClient(uuid: String): Boolean {
        return clientSet.contains(uuid)
    }

    private fun sendMessage(server: SocketIOServer) {
        val userData: MutableList<Map<String, Any>> = ArrayList(userMap.size)
        userMap.forEach { (_: Long?, userInfo: UserInfo) -> userData.add(createUserMap(userInfo)) }
        HandUtils.sendGlobalMessage(server, "[ONLINE]",
            Collections.singletonMap<String, List<Map<String, Any>>>("data", userData)
        )
    }

    private fun createUserMap(userInfo: UserInfo): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap(4)
        map["uid"] = userInfo.uid
        map["uuids"] = ArrayList(userInfo.uuids)
        map["login"] = userInfo.login
        map["platform"] = userInfo.platform
        return map
    }

    private class UserInfo(val uid: Long, uuid: String, platform: String) {
        val uuids: MutableSet<String> = ConcurrentHashMap.newKeySet()

        @Volatile
        var login: Boolean = true

        @Volatile
        var platform: String

        init {
            uuids.add(uuid)
            this.platform = platform
        }

        fun addUuid(uuid: String) {
            uuids.add(uuid)
        }

        fun removeUuid(uuid: String): Boolean {
            uuids.remove(uuid)
            return uuids.isEmpty()
        }
    }
}
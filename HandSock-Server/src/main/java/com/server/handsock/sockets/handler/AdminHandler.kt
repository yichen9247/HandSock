package com.server.handsock.sockets.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.server.handsock.admin.service.*
import com.server.handsock.services.AuthService
import com.server.handsock.services.ClientService
import com.server.handsock.sockets.eventer.OnlineEvent
import com.server.handsock.utils.ConsoleUtils
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdminHandler @Autowired constructor(
    private val authService: AuthService,
    private val clientService: ClientService,
    private val serverLogService: ServerLogService,
    private val serverUserService: ServerUserService,
    private val serverChatService: ServerChatService,
    private val serverDashService: ServerDashService,
    private val serverNoticeService: ServerNoticeService,
    private val serverUploadService: ServerUploadService,
    private val serverReportService: ServerReportService,
    private val serverBannerService: ServerBannerService,
    private val serverChannelService: ServerChannelService
) {
    fun handleAdminRequest(client: SocketIOClient, data: Map<String?, Any>?, ackSender: AckRequest, action: String?, uidKey: String, valueKey: String?) {
        ackSender.sendAckData(authService.validAdminStatusBySocket(client) {
            try {
                val value = if (data != null) getClientData(data, uidKey) else "none"
                val valueData = if (data != null && valueKey != null) getClientData(data, valueKey) else null

                val actionMap = mutableMapOf<String, () -> Any>().apply {
                    // Admin Delete
                    put("deleteUser") { deleteUser(value) }
                    put("deleteChan") { deleteChan(value) }
                    put("deleteChat") { deleteChat(value) }
                    put("deleteRepo") { deleteRepo(value) }
                    put("deleteBanner") { deleteBanner(value) }
                    put("deleteNotice") { deleteNotice(value) }
                    put("deleteUpload") { deleteUpload(value) }
                    put("deleteSystemLogs") { serverLogService.deleteSystemLogs() }

                    // Admin Create
                    put("createChan") { createChan(data!!) }
                    put("createBanner") { createBanner(data!!) }
                    put("createNotice") { createNotice(data!!) }
                    put("updateChanInfo") { updateChanInfo(data) }
                    put("updateBannerInfo") { updateBannerInfo(data) }
                    put("updateNoticeInfo") { updateNoticeInfo(data) }
                    put("updateUserInfo") { updateUserInfo(data) }

                    // Admin Get
                    put("getUserList") { getUserList(data) }
                    put("getChatList") { getChatList(data) }
                    put("getChanList") { getChanList(data) }
                    put("getBannerList") { getBannerList(data) }
                    put("getNoticeList") { getNoticeList(data) }
                    put("getRepoList") { getRepoList(data) }
                    put("getSystemLogs") { serverLogService.systemLogs }
                    put("getUploadList") { getUploadList(data) }
                    put("getChatContent") { getChatContent(value) }
                }

                if (valueData != null) {
                    actionMap.apply {
                        put("updateUserPassword") { updateUserPassword(value, valueData) }
                        put("updateChanOpenStatus") { updateChanOpenStatus(value, valueData) }
                        put("updateChanActiveStatus") { updateChanActiveStatus(value, valueData) }
                        put("updateUserTabooStatus") { updateUserTabooStatus(value, valueData) }
                    }
                }

                action?.let { actionKey ->
                    actionMap[actionKey]?.let { handler -> handler()
                    } ?: HandUtils.handleResultByCode(400, null, "未知操作")
                } ?: HandUtils.handleResultByCode(400, null, "操作类型未指定")
            } catch (e: Exception) {
                HandUtils.printErrorLog(e)
            }
        })
    }

    private fun getClientData(data: Map<String?, Any>, key: String): Any? {
        return data[key]
    }

    private fun deleteUser(value: Any?): Any {
        return serverUserService.deleteUser(value.toString().toLong())
    }

    private fun deleteChan(value: Any?): Any {
        return serverChannelService.deleteChan(value.toString().toLong())
    }

    private fun deleteRepo(value: Any?): Any {
        return serverReportService.deleteReport(value.toString())
    }

    private fun deleteUpload(value: Any?): Any {
        return serverUploadService.deleteUpload(value.toString())
    }

    private fun deleteBanner(value: Any?): Any {
        return serverBannerService.deleteBanner(value.toString().toInt())
    }

    private fun deleteNotice(value: Any?): Any {
        return serverNoticeService.deleteNotice(value.toString().toInt())
    }

    private fun deleteChat(value: Any?): Any {
        return serverChatService.deleteChat(value.toString())
    }

    private fun getChatContent(value: Any?): Any {
        return serverChatService.getChatContent(value.toString())
    }

    private fun handleListRequest(serviceCall: (Int, Int) -> Any, data: Map<String?, Any>?): Any {
        val page = clientService.getClientData(data!!, "page").toInt()
        val limit = clientService.getClientData(data, "limit").toInt()
        return serviceCall(page, limit)
    }

    private fun getUserList(data: Map<String?, Any>?) =
        handleListRequest(serverUserService::getUserList, data)

    private fun getChatList(data: Map<String?, Any>?) =
        handleListRequest(serverChatService::getChatList, data)

    private fun getChanList(data: Map<String?, Any>?) =
        handleListRequest(serverChannelService::getChanList, data)

    private fun getRepoList(data: Map<String?, Any>?) =
        handleListRequest(serverReportService::getReportList, data)

    private fun getBannerList(data: Map<String?, Any>?) =
        handleListRequest(serverBannerService::getBannerList, data)

    private fun getNoticeList(data: Map<String?, Any>?) =
        handleListRequest(serverNoticeService::getNoticeList, data)

    private fun getUploadList(data: Map<String?, Any>?) =
        handleListRequest(serverUploadService::getUploadList, data)

    private fun updateUserPassword(value: Any?, valueData: Any): Any {
        return serverUserService.updateUserPassword(
            uid = value.toString().toLong(),
            password = valueData.toString()
        )
    }

    private fun updateChanOpenStatus(value: Any?, valueData: Any): Any {
        return serverChannelService.updateChanOpenStatus(
            gid = value.toString().toLong(),
            status = valueData.toString().toInt()
        )
    }

    private fun updateChanActiveStatus(value: Any?, valueData: Any): Any {
        return serverChannelService.updateChanActiveStatus(
            gid = value.toString().toLong(),
            status = valueData.toString().toInt()
        )
    }

    private fun updateUserTabooStatus(value: Any?, valueData: Any): Any {
        return serverUserService.updateUserTabooStatus(
            uid = value.toString().toLong(),
            status = valueData.toString()
        )
    }

    private fun createChan(data: Map<String?, Any>): Any {
        return serverChannelService.createChan(
            clientService.getClientData(
                data = data,
                key = "gid"
            ).toLong(),
            clientService.getClientData(
                data = data,
                key = "name"
            ),
            clientService.getClientData(
                data = data,
                key = "avatar"
            ),
            clientService.getClientData(
                data = data,
                key = "notice"
            ),
            clientService.getClientData(
                data = data,
                key = "aiRole"
            ).toBoolean()
        )
    }

    private fun createBanner(data: Map<String?, Any>): Any {
        return serverBannerService.createBanner(
            clientService.getClientData(
                data = data,
                key = "name"
            ),
            clientService.getClientData(
                data = data,
                key = "href"
            ),
            clientService.getClientData(
                data = data,
                key = "image"
            ),
        )
    }

    private fun createNotice(data: Map<String?, Any>): Any {
        return serverNoticeService.createNotice(
            clientService.getClientData(
                data = data,
                key = "title"
            ),
            clientService.getClientData(
                data = data,
                key = "content"
            )
        )
    }

    private fun updateUserInfo(data: Map<String?, Any>?): Any {
        return serverUserService.updateUserInfo(
            clientService.getClientData(data!!, "uid").toLong(),
            clientService.getClientData(
                data = data,
                key = "username"
            ),
            clientService.getClientData(data, "nick"),
            clientService.getClientData(data, "avatar"),
            clientService.getClientData(
                data = data,
                key = "robot"
            ).toBoolean()
        )
    }

    private fun updateChanInfo(data: Map<String?, Any>?): Any {
        return serverChannelService.updateChan(
            clientService.getClientData(
                data = data!!,
                key = "gid"
            ).toLong(),
            clientService.getClientData(
                data, "name"
            ),
            clientService.getClientData(
                data = data,
                key = "avatar"
            ),
            clientService.getClientData(
                data = data,
                key = "notice"
            ),
            clientService.getClientData(
                data, "aiRole"
            ).toBoolean()
        )
    }

    private fun updateBannerInfo(data: Map<String?, Any>?): Any {
        return serverBannerService.updateBanner(
            clientService.getClientData(
                data = data!!,
                key = "bid"
            ).toInt(),
            clientService.getClientData(
                data, "name"
            ),
            clientService.getClientData(
                data = data,
                key = "href"
            ),
            clientService.getClientData(
                data = data,
                key = "image"
            )
        )
    }

    private fun updateNoticeInfo(data: Map<String?, Any>?): Any {
        return serverNoticeService.updateNotice(
            clientService.getClientData(
                data = data!!,
                key = "nid"
            ).toInt(),
            clientService.getClientData(
                data, "title"
            ),
            clientService.getClientData(
                data = data,
                key = "content"
            )
        )
    }

    fun getDashboardData(client: SocketIOClient, ackSender: AckRequest) {
        authService.validClientStatusBySocket(client) {
            ackSender.sendAckData(serverDashService.dashboardData)
        }
    }

    fun forceReloadClient(server: SocketIOServer?, client: SocketIOClient, ackSender: AckRequest, event: String, onlineEvent: OnlineEvent) {
        authService.validClientStatusBySocket(client) {
            if (event == "[RE:HISTORY:CLEAR]") serverChatService.clearAllChatHistory()
            onlineEvent.clearClient()
            HandUtils.sendGlobalMessage(server!!, event, null)
            ackSender.sendAckData(object : HashMap<Any?, Any?>() {
                init {
                    put("code", 200)
                    put("message", "指令已发出")
                }
            })
            ConsoleUtils.printInfoLog("Force Client Event：$event")
        }
    }
}
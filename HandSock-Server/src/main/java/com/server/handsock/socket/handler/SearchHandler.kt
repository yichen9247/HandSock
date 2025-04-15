package com.server.handsock.socket.handler

import com.corundumstudio.socketio.AckRequest
import com.server.handsock.client.service.ClientChannelService
import com.server.handsock.client.service.ClientChatService
import com.server.handsock.client.service.ClientNoticeService
import com.server.handsock.client.service.ClientUserService
import com.server.handsock.common.data.CommonSearchPage
import com.server.handsock.common.data.SocketSearchChannel
import com.server.handsock.common.service.CommentService
import com.server.handsock.common.service.ForumService
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SearchHandler @Autowired constructor(
    private val forumService: ForumService,
    private val commentService: CommentService,
    private val clientChatService: ClientChatService,
    private val clientUserService: ClientUserService,
    private val clientNoticeService: ClientNoticeService,
    private val clientChannelService: ClientChannelService
) {
    fun handleSearchGroup(data: SocketSearchChannel, ackRequest: AckRequest) {
        try {
            ackRequest.sendAckData(
                if (data.gid == null)
                    HandUtils.printParamError()
                else clientChannelService.searchGroupByGid(data.gid)
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

    fun handleSearchAllHistory(data: SocketSearchChannel, ackRequest: AckRequest) {
        try {
            ackRequest.sendAckData(
                if (data.gid == null)
                    HandUtils.printParamError()
                else clientChatService.searchAllChatHistory(data.gid)
            )
        } catch (e: Exception) {
            ackRequest.sendAckData(HandUtils.printErrorLog(e))
        }
    }

    fun handleSearchAllNotice(ackRequest: AckRequest) {
        try {
            ackRequest.sendAckData(
                clientNoticeService.searchAllNotice()
            )
        } catch (e: Exception) {
            ackRequest.sendAckData(HandUtils.printErrorLog(e))
        }
    }

    fun handleSearchForumPost(data: CommonSearchPage, ackRequest: AckRequest) {
        try {
            ackRequest.sendAckData(
                if (data.page == null || data.limit == null || data.page <= 0 || data.limit <= 0)
                    HandUtils.printParamError()
                else forumService.searchForumPost(
                    page = data.page,
                    limit = data.limit
                )
            )
        } catch (e: Exception) {
            ackRequest.sendAckData(HandUtils.printErrorLog(e))
        }
    }

    fun handleSearchPostComment(data: CommonSearchPage, ackRequest: AckRequest) {
        try {
            ackRequest.sendAckData(
                if (data.pid == null || data.page == null || data.limit == null || data.pid <= 0 ||data.page <= 0 || data.limit <= 0)
                    HandUtils.printParamError()
                else commentService.searchPostComment(
                    pid = data.pid,
                    page = data.page,
                    limit = data.limit
                )
            )
        } catch (e: Exception) {
            ackRequest.sendAckData(HandUtils.printErrorLog(e))
        }
    }
}

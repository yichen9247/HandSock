package com.server.handsock.client.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.client.man.ClientChatManage
import com.server.handsock.common.dao.MessageDao
import com.server.handsock.common.model.MessageModel
import com.server.handsock.common.utils.ConsoleUtils
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.common.utils.IDGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ClientChatService @Autowired constructor(private val messageDao: MessageDao) {
    @Transactional
    open fun insertChatMessage(type: String, uid: Long, gid: Long, address: String, content: String): Map<String, Any> {
        if (content.length > 200) return HandUtils.handleResultByCode(400, null, "消息过长")
        val messageModel = MessageModel()
        val result = ClientChatManage(HandUtils, ConsoleUtils, IDGenerator).insertChatMessage(messageModel, type, uid, gid, address, content)
        return if (messageDao.insert(messageModel) > 0) {
            HandUtils.handleResultByCode(200, result, "发送成功")
        } else HandUtils.handleResultByCode(400, null, "发送失败")
    }

    @Transactional
    open fun searchAllChatHistory(gid: Long): Map<String, Any> {
        val wrapper = QueryWrapper<MessageModel>()
        wrapper.orderByAsc("time").eq("gid", gid)
        return HandUtils.handleResultByCode(200, messageDao.selectList(wrapper), "获取成功")
    }
}

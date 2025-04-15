package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.common.dao.MessageDao
import com.server.handsock.common.model.MessageModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ServerChatService @Autowired constructor(private val messageDao: MessageDao) {
    @Transactional
    open fun getChatContent(sid: String): Map<String, Any> {
        val messageModel = messageDao.selectOne(QueryWrapper<MessageModel>().eq("sid", sid))
        return if (messageModel != null) {
            HandUtils.handleResultByCode(200, messageModel, "获取成功")
        } else HandUtils.handleResultByCode(501, null, "内容已被删除")
    }

    @Transactional
    open fun getChatList(page: Int, limit: Int): Map<String, Any> {
        val pageObj = Page<MessageModel>(page.toLong(), limit.toLong())
        val wrapper = QueryWrapper<MessageModel>().orderByDesc("time")
        val queryResult = messageDao.selectPage(pageObj, wrapper)
        return HandUtils.handleResultByCode(200,  mapOf(
            "total" to queryResult.total,
            "items" to queryResult.records
        ), "获取成功")
    }

    @Transactional
    open fun deleteChat(sid: String): Map<String, Any> {
        val messageModel = messageDao.selectOne(QueryWrapper<MessageModel>().eq("sid", sid))
        return if (messageModel != null) {
            if (messageDao.deleteById(sid) > 0) {
                HandUtils.handleResultByCode(200, null, "删除成功")
            } else HandUtils.handleResultByCode(400, null, "删除失败")
        } else HandUtils.handleResultByCode(501, null, "消息已被删除")
    }

    @Transactional
    open fun clearAllChatHistory() {
        if (messageDao.delete(null) > 0) {
            HandUtils.handleResultByCode(200, null, "清空聊天记录成功")
        } else HandUtils.handleResultByCode(400, null, "清空聊天记录成功失败")
    }
}

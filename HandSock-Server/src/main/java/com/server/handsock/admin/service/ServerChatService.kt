package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.admin.dao.ServerChatDao
import com.server.handsock.admin.mod.ServerChatModel
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class ServerChatService @Autowired constructor(private val serverChatDao: ServerChatDao) {

    fun getChatContent(sid: String?): Map<String, Any> {
        try {
            val serverChatModel = serverChatDao.selectOne(QueryWrapper<ServerChatModel>().eq("sid", sid))
            return if (serverChatModel != null) {
                HandUtils.handleResultByCode(200, serverChatModel, "获取成功")
            } else HandUtils.handleResultByCode(501, null, "内容已被删除")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun getChatList(page: Int, limit: Int): Map<String, Any> {
        try {
            val serverChatModelList = serverChatDao.selectList(null)
            val total = serverChatModelList.size
            val startWith = (page - 1) * limit
            serverChatModelList.reverse()
            val endWith = min((startWith + limit).toDouble(), serverChatModelList.size.toDouble()).toInt()
            val subList: List<ServerChatModel?> = serverChatModelList.subList(startWith, endWith)
            return HandUtils.handleResultByCode(200, object : HashMap<Any?, Any?>() {
                init {
                    put("items", subList)
                    put("total", total)
                }
            }, "获取成功")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun deleteChat(sid: String?): Map<String, Any> {
        try {
            val serverChatModel = serverChatDao.selectOne(QueryWrapper<ServerChatModel>().eq("sid", sid))
            return if (serverChatModel != null) {
                if (serverChatDao.deleteById(sid) > 0) {
                    HandUtils.handleResultByCode(200, null, "删除成功")
                } else HandUtils.handleResultByCode(400, null, "删除失败")
            } else HandUtils.handleResultByCode(501, null, "消息已被删除")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun clearAllChatHistory() {
        try {
            if (serverChatDao.delete(null) > 0) {
                HandUtils.handleResultByCode(200, null, "清空聊天记录成功")
            } else HandUtils.handleResultByCode(400, null, "清空聊天记录成功失败")
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
        }
    }
}

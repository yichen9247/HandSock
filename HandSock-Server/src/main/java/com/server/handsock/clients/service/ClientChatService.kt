package com.server.handsock.clients.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.clients.dao.ClientChatDao
import com.server.handsock.clients.man.ClientChatManage
import com.server.handsock.clients.mod.ClientChatModel
import com.server.handsock.utils.ConsoleUtils
import com.server.handsock.utils.HandUtils
import com.server.handsock.utils.IDGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClientChatService @Autowired constructor(private val clientChatDao: ClientChatDao) {
    fun insertChatMessage(type: String, uid: Long, gid: Long, address: String, content: String): Map<String, Any> {
        try {
            if (content.length > 200) return HandUtils.handleResultByCode(400, null, "消息过长")
            val clientChatModel = ClientChatModel()
            val result = ClientChatManage(HandUtils, ConsoleUtils, IDGenerator).insertChatMessage(clientChatModel, type, uid, gid, address, content)
            return if (clientChatDao.insert(clientChatModel) > 0) {
                HandUtils.handleResultByCode(200, result, "发送成功")
            } else HandUtils.handleResultByCode(400, null, "发送失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun searchAllChatHistory(gid: Long?): Map<String, Any> {
        try {
            val wrapper = QueryWrapper<ClientChatModel>()
            wrapper.orderByAsc("time")
            return HandUtils.handleResultByCode(200, clientChatDao.selectList(wrapper.eq("gid", gid)), "获取成功")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }
}

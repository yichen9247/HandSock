package com.server.handsock.clients.service

import com.server.handsock.clients.dao.ClientNoticeDao
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClientNoticeService @Autowired constructor(private val clientNoticeDao: ClientNoticeDao) {
    fun searchAllNotice(): Map<String, Any> {
        try {
            val noticeList = clientNoticeDao.selectList(null)
            noticeList.reverse()
            return HandUtils.handleResultByCode(200, noticeList, "获取成功")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }
}
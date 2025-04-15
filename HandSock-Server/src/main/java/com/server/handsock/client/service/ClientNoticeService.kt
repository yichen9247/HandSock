package com.server.handsock.client.service

import com.server.handsock.common.dao.NoticeDao
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ClientNoticeService @Autowired constructor(private val noticeDao: NoticeDao) {
    @Transactional
    open fun searchAllNotice(): Map<String, Any> {
        val noticeList = noticeDao.selectList(null)
        return HandUtils.handleResultByCode(200, noticeList, "获取成功")
    }
}
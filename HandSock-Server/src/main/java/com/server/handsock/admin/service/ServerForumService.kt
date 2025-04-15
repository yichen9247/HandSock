package com.server.handsock.admin.service

import com.server.handsock.common.dao.ForumDao
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ServerForumService @Autowired constructor(
    private val forumDao: ForumDao
) {
    @Transactional
    open fun deleteForumPost(pid: Int): Map<String, Any> {
        return if (forumDao.deleteById(pid) > 0) {
            HandUtils.handleResultByCode(200, null, "删除成功")
        } else HandUtils.handleResultByCode(400, null, "删除失败")
    }
}
package com.server.handsock.client.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.client.man.ClientCommentManage
import com.server.handsock.common.dao.CommentDao
import com.server.handsock.common.dao.ForumDao
import com.server.handsock.common.model.CommentModel
import com.server.handsock.common.model.ForumModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ClientCommentService @Autowired constructor(
    private val forumDao: ForumDao,
    private val commentDao: CommentDao
) {
    @Transactional
    open fun addPostComment(uid: Long?, pid: Int?, parent: Int?, content: String?): Map<String, Any> {
        val commentModel = CommentModel()
        forumDao.selectOne(QueryWrapper<ForumModel>().eq("pid", pid))
            ?: return HandUtils.handleResultByCode(400, null, "帖子不存在")
        if (parent != null && commentDao.selectOne(QueryWrapper<CommentModel>().eq("cid", parent)) == null)
            return HandUtils.handleResultByCode(500, null, "父级评论不存在")
        ClientCommentManage().insertPostComment(commentModel, pid, parent, uid, content)
        return if (commentDao.insert(commentModel) > 0) {
            HandUtils.handleResultByCode(200, null, "发送成功")
        } else HandUtils.handleResultByCode(400, null, "发送失败")
    }
}
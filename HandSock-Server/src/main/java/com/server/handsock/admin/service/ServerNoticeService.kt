package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.admin.man.ServerNoticeManage
import com.server.handsock.common.dao.NoticeDao
import com.server.handsock.common.model.NoticeModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ServerNoticeService @Autowired constructor(private val noticeDao: NoticeDao) {
    @Transactional
    open fun getNoticeList(page: Int, limit: Int): Map<String, Any> {
        val pageObj = Page<NoticeModel>(page.toLong(), limit.toLong())
        val wrapper = QueryWrapper<NoticeModel>().orderByDesc("time")
        val queryResult = noticeDao.selectPage(pageObj, wrapper)
        return HandUtils.handleResultByCode(200,  mapOf(
            "total" to queryResult.total,
            "items" to queryResult.records
        ), "获取成功")
    }

    @Transactional
    open fun deleteNotice(nid: Int): Map<String, Any> {
        return if (noticeDao.deleteById(nid) > 0) {
            HandUtils.handleResultByCode(200, null, "删除成功")
        } else HandUtils.handleResultByCode(400, null, "删除失败")
    }

    @Transactional
    open fun updateNotice(nid: Int, title: String, content: String): Map<String, Any> {
        if (noticeDao.selectOne(QueryWrapper<NoticeModel>().eq("nid", nid)) == null) return HandUtils.handleResultByCode(409, null, "公告不存在")
        val noticeModel = NoticeModel()
        ServerNoticeManage().updateNotice(noticeModel, nid, title, content)
        return if (noticeDao.updateById(noticeModel) > 0) {
            HandUtils.handleResultByCode(200, null, "修改成功")
        } else HandUtils.handleResultByCode(400, null, "修改失败")
    }

    @Transactional
    open fun createNotice(title: String, content: String): Map<String, Any> {
        val noticeModel = NoticeModel()
        ServerNoticeManage().setNotice(noticeModel, title, content)
        return if (noticeDao.insert(noticeModel) > 0) {
            HandUtils.handleResultByCode(200, null, "创建成功")
        } else HandUtils.handleResultByCode(400, null, "创建失败")
    }
}
package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.admin.man.ServerChannelManage
import com.server.handsock.common.dao.ChannelDao
import com.server.handsock.common.model.ChannelModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ServerChannelService @Autowired constructor(private val channelDao: ChannelDao) {
    @Transactional
    open fun getChanList(page: Int, limit: Int): Map<String, Any> {
        val wrapper = QueryWrapper<ChannelModel>()
        val pageObj = Page<ChannelModel>(page.toLong(), limit.toLong())
        val queryResult = channelDao.selectPage(pageObj, wrapper)
        return HandUtils.handleResultByCode(200,  mapOf(
            "total" to queryResult.total,
            "items" to queryResult.records
        ), "获取成功")
    }

    @Transactional
    open fun deleteChan(gid: Long): Map<String, Any> {
        if (gid == 0L) return HandUtils.handleResultByCode(409, null, "主频道不可操作")
        return if (channelDao.deleteById(gid) > 0) {
            HandUtils.handleResultByCode(200, null, "删除成功")
        } else HandUtils.handleResultByCode(400, null, "删除失败")
    }

    @Transactional
    open fun updateChan(gid: Long, name: String, avatar: String, notice: String, aiRole: Boolean?): Map<String, Any> {
        if (channelDao.selectOne(QueryWrapper<ChannelModel>().eq("gid", gid)) == null) return HandUtils.handleResultByCode(409, null, "频道不存在")
        val channelModel = ChannelModel()
        ServerChannelManage().setChan(channelModel, gid, name, avatar, notice, aiRole)
        return if (channelDao.updateById(channelModel) > 0) {
            HandUtils.handleResultByCode(200, null, "修改成功")
        } else HandUtils.handleResultByCode(400, null, "修改失败")
    }

    @Transactional
    open fun createChan(gid: Long, name: String, avatar: String, notice: String, aiRole: Boolean): Map<String, Any> {
        if (channelDao.selectOne(QueryWrapper<ChannelModel>().eq("gid", gid)) != null) return HandUtils.handleResultByCode(409, null, "频道已存在")
        val channelModel = ChannelModel()
        ServerChannelManage().setChan(channelModel, gid, name, avatar, notice, aiRole)
        return if (channelDao.insert(channelModel) > 0) {
            HandUtils.handleResultByCode(200, null, "创建成功")
        } else HandUtils.handleResultByCode(400, null, "创建失败")
    }

    @Transactional
    open fun updateChanOpenStatus(gid: Long, status: Int): Map<String, Any> {
        if (gid == 0L) return HandUtils.handleResultByCode(409, null, "主频道不可操作")
        val channelModel = ChannelModel()
        ServerChannelManage().updateChanOpenStatus(channelModel, gid, status)
        return if (channelDao.updateById(channelModel) > 0) {
            HandUtils.handleResultByCode(200, null, "设置成功")
        } else HandUtils.handleResultByCode(400, null, "设置失败")
    }

    @Transactional
    open fun updateChanActiveStatus(gid: Long, status: Int): Map<String, Any> {
        if (gid == 0L) return HandUtils.handleResultByCode(409, null, "主频道不可操作")
        val channelModel = ChannelModel()
        ServerChannelManage().updateChanActiveStatus(channelModel, gid, status)
        return if (channelDao.updateById(channelModel) > 0) {
            HandUtils.handleResultByCode(200, null, "设置成功")
        } else HandUtils.handleResultByCode(400, null, "设置失败")
    }
}

package com.server.handsock.client.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.common.dao.ChannelDao
import com.server.handsock.common.model.ChannelModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ClientChannelService @Autowired constructor(private val channelDao: ChannelDao) {
    @Transactional
    open fun searchAllGroup(): Map<String, Any> {
        val channelModelList = channelDao.selectList(QueryWrapper<ChannelModel>().eq("active", 1))
        val history: MutableList<Map<String, Any>> = ArrayList()
        for (clientChannelModel in channelModelList) {
            history.add(object : HashMap<String, Any>() {
                init {
                    clientChannelModel.gid?.let { put("gid", it) }
                }
            })
        }
        val result = HandUtils.handleResultByCode(200, channelModelList, "获取成功").apply {
            "latest" to history
        }
        return result
    }

    @Transactional
    open fun searchGroupByGid(gid: Long): Map<String, Any> {
        val groupSelect = if (gid == 0L) {
            channelDao.selectOne(QueryWrapper<ChannelModel>().eq("home", 1))
        } else channelDao.selectOne(QueryWrapper<ChannelModel>().eq("gid", gid))
        return if (groupSelect == null) {
            HandUtils.handleResultByCode(404, null, "未找到频道")
        } else HandUtils.handleResultByCode(200, groupSelect, "获取成功")
    }

    @Transactional
    open fun getChanOpenStatus(gid: Long): Boolean {
        val status = channelDao.selectOne(QueryWrapper<ChannelModel>().eq("gid", gid)).open
        return status == 1
    }
}

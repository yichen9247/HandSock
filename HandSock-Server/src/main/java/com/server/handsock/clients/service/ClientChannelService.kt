package com.server.handsock.clients.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.clients.dao.ClientChannelDao
import com.server.handsock.clients.mod.ClientChannelModel
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClientChannelService @Autowired constructor(private val clientChannelDao: ClientChannelDao) {
    fun searchAllGroup(): Map<String, Any> {
        try {
            val clientChannelModelList = clientChannelDao.selectList(QueryWrapper<ClientChannelModel>().eq("active", 1))

            val history: MutableList<Map<String, Any>> = ArrayList()
            for (clientChannelModel in clientChannelModelList) {
                history.add(object : HashMap<String, Any>() {
                    init {
                        clientChannelModel.gid?.let { put("gid", it) }
                    }
                })
            }
            val result = HandUtils.handleResultByCode(200, clientChannelModelList, "获取成功").apply {
                "latest" to history
            }
            return result
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun searchGroupByGid(gid: Long): Map<String, Any> {
        try {
            val groupSelect = if (gid == 0L) {
                clientChannelDao.selectOne(QueryWrapper<ClientChannelModel>().eq("home", 1))
            } else clientChannelDao.selectOne(QueryWrapper<ClientChannelModel>().eq("gid", gid))
            return if (groupSelect == null) {
                HandUtils.handleResultByCode(404, null, "未找到频道")
            } else HandUtils.handleResultByCode(200, groupSelect, "获取成功")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun getChanOpenStatus(gid: Long?): Boolean {
        try {
            val status = clientChannelDao.selectOne(QueryWrapper<ClientChannelModel>().eq("gid", gid)).open
            return status == 1
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
            return false
        }
    }
}

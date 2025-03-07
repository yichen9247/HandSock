package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.admin.dao.ServerChannelDao
import com.server.handsock.admin.dao.ServerChatDao
import com.server.handsock.admin.man.ServerChannelManage
import com.server.handsock.admin.mod.ServerChannelModel
import com.server.handsock.admin.mod.ServerChatModel
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class ServerChannelService @Autowired constructor(
    private val serverChatDao: ServerChatDao,
    private val serverChannelDao: ServerChannelDao
) {
    fun getChanList(page: Int, limit: Int): Map<String, Any> {
        try {
            val serverChannelModelList = serverChannelDao.selectList(null)
            val total = serverChannelModelList.size
            val startWith = (page - 1) * limit
            val endWith = min((startWith + limit).toDouble(), serverChannelModelList.size.toDouble()).toInt()
            val subList: List<ServerChannelModel?> = serverChannelModelList.subList(startWith, endWith)
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

    fun deleteChan(gid: Long): Map<String, Any> {
        if (gid == 0L) return HandUtils.handleResultByCode(409, null, "主频道不可操作")
        try {
            val serverChatModel = serverChatDao.selectList(QueryWrapper<ServerChatModel>().eq("gid", gid))
            if (serverChatModel.isEmpty()) {
                if (serverChannelDao.deleteById(gid) > 0) return HandUtils.handleResultByCode(200, null, "删除成功")
            } else {
                if (serverChannelDao.deleteById(gid) > 0 && serverChatDao.delete(QueryWrapper<ServerChatModel>().eq("gid", gid)) > 0) return HandUtils.handleResultByCode(200, null, "删除成功")
            }
            return HandUtils.handleResultByCode(400, null, "删除失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun updateChan(gid: Long?, name: String?, avatar: String?, notice: String?, aiRole: Boolean?): Map<String, Any> {
        try {
            if (serverChannelDao.selectOne(QueryWrapper<ServerChannelModel>().eq("gid", gid)) == null) return HandUtils.handleResultByCode(409, null, "频道不存在")
            val serverChannelModel = ServerChannelModel()
            ServerChannelManage().setChan(serverChannelModel, gid, name, avatar, notice, aiRole)
            return if (serverChannelDao.updateById(serverChannelModel) > 0) {
                HandUtils.handleResultByCode(200, null, "修改成功")
            } else HandUtils.handleResultByCode(400, null, "修改失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun createChan(gid: Long?, name: String?, avatar: String?, notice: String?, aiRole: Boolean?): Map<String, Any> {
        try {
            if (serverChannelDao.selectOne(QueryWrapper<ServerChannelModel>().eq("gid", gid)) != null) return HandUtils.handleResultByCode(409, null, "频道已存在")
            val serverChannelModel = ServerChannelModel()
            ServerChannelManage().setChan(serverChannelModel, gid, name, avatar, notice, aiRole)
            return if (serverChannelDao.insert(serverChannelModel) > 0) {
                HandUtils.handleResultByCode(200, null, "创建成功")
            } else HandUtils.handleResultByCode(400, null, "创建失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun updateChanOpenStatus(gid: Long, status: Int?): Map<String, Any> {
        if (gid == 0L) return HandUtils.handleResultByCode(409, null, "主频道不可操作")
        try {
            val serverChannelModel = ServerChannelModel()
            ServerChannelManage().updateChanOpenStatus(serverChannelModel, gid, status)
            return if (serverChannelDao.updateById(serverChannelModel) > 0) {
                HandUtils.handleResultByCode(200, null, "设置成功")
            } else HandUtils.handleResultByCode(400, null, "设置失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun updateChanActiveStatus(gid: Long, status: Int?): Map<String, Any> {
        if (gid == 0L) return HandUtils.handleResultByCode(409, null, "主频道不可操作")
        try {
            val serverChannelModel = ServerChannelModel()
            ServerChannelManage().updateChanActiveStatus(serverChannelModel, gid, status)
            return if (serverChannelDao.updateById(serverChannelModel) > 0) {
                HandUtils.handleResultByCode(200, null, "设置成功")
            } else HandUtils.handleResultByCode(400, null, "设置失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }
}

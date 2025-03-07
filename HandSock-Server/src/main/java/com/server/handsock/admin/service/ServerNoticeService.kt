package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.admin.dao.ServerNoticeDao
import com.server.handsock.admin.man.ServerNoticeManage
import com.server.handsock.admin.mod.ServerNoticeModel
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class ServerNoticeService @Autowired constructor(private val serverNoticeDao: ServerNoticeDao) {
    fun getNoticeList(page: Int, limit: Int): Map<String, Any> {
        try {
            val serverNoticeModelList = serverNoticeDao.selectList(null)
            val total = serverNoticeModelList.size
            val startWith = (page - 1) * limit
            serverNoticeModelList.reverse()
            val endWith = min((startWith + limit).toDouble(), serverNoticeModelList.size.toDouble()).toInt()
            val subList: List<ServerNoticeModel?> = serverNoticeModelList.subList(startWith, endWith)
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

    fun deleteNotice(nid: Int): Map<String, Any> {
        return try {
            if (serverNoticeDao.deleteById(nid) > 0) {
                HandUtils.handleResultByCode(200, null, "删除成功")
            } else HandUtils.handleResultByCode(400, null, "删除失败")
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
        }
    }

    fun updateNotice(nid: Int, title: String?, content: String?): Map<String, Any> {
        try {
            if (serverNoticeDao.selectOne(QueryWrapper<ServerNoticeModel>().eq("nid", nid)) == null) return HandUtils.handleResultByCode(409, null, "公告不存在")
            val serverNoticeModel = ServerNoticeModel()
            ServerNoticeManage().updateNotice(serverNoticeModel, nid, title, content)
            return if (serverNoticeDao.updateById(serverNoticeModel) > 0) {
                HandUtils.handleResultByCode(200, null, "修改成功")
            } else HandUtils.handleResultByCode(400, null, "修改失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun createNotice(title: String?, content: String?): Map<String, Any> {
        try {
            val serverNoticeModel = ServerNoticeModel()
            ServerNoticeManage().setNotice(serverNoticeModel, title, content)
            return if (serverNoticeDao.insert(serverNoticeModel) > 0) {
                HandUtils.handleResultByCode(200, null, "创建成功")
            } else HandUtils.handleResultByCode(400, null, "创建失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }
}
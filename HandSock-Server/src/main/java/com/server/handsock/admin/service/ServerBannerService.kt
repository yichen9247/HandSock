package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.admin.dao.ServerBannerDao
import com.server.handsock.admin.man.ServerBannerManage
import com.server.handsock.admin.mod.ServerBannerModel
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class ServerBannerService @Autowired constructor(private val serverBannerDao: ServerBannerDao) {
    fun getBannerList(page: Int, limit: Int): Map<String, Any> {
        try {
            val serverBannerModelList = serverBannerDao.selectList(null)
            val total = serverBannerModelList.size
            val startWith = (page - 1) * limit
            val endWith = min((startWith + limit).toDouble(), serverBannerModelList.size.toDouble()).toInt()
            val subList: List<ServerBannerModel?> = serverBannerModelList.subList(startWith, endWith)
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

    fun deleteBanner(bid: Int): Map<String, Any> {
        return try {
            if (serverBannerDao.deleteById(bid) > 0) {
                HandUtils.handleResultByCode(200, null, "删除成功")
            } else HandUtils.handleResultByCode(400, null, "删除失败")
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
        }
    }

    fun updateBanner(bid: Int, name: String?, href: String?, image: String?): Map<String, Any> {
        try {
            if (serverBannerDao.selectOne(QueryWrapper<ServerBannerModel>().eq("bid", bid)) == null) return HandUtils.handleResultByCode(409, null, "轮播不存在")
            val serverBannerModel = ServerBannerModel()
            ServerBannerManage().updateBanner(serverBannerModel, bid, name, href, image)
            return if (serverBannerDao.updateById(serverBannerModel) > 0) {
                HandUtils.handleResultByCode(200, null, "修改成功")
            } else HandUtils.handleResultByCode(400, null, "修改失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun createBanner(name: String?, href: String?, image: String?): Map<String, Any> {
        try {
            val serverBannerModel = ServerBannerModel()
            ServerBannerManage().setBanner(serverBannerModel, name, href, image)
            return if (serverBannerDao.insert(serverBannerModel) > 0) {
                HandUtils.handleResultByCode(200, null, "创建成功")
            } else HandUtils.handleResultByCode(400, null, "创建失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }
}
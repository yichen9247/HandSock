package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.admin.man.ServerBannerManage
import com.server.handsock.common.dao.BannerDao
import com.server.handsock.common.model.BannerModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ServerBannerService @Autowired constructor(private val bannerDao: BannerDao) {
    @Transactional
    open fun getBannerList(page: Int, limit: Int): Map<String, Any> {
        val wrapper = QueryWrapper<BannerModel>()
        val pageObj = Page<BannerModel>(page.toLong(), limit.toLong())
        val queryResult = bannerDao.selectPage(pageObj, wrapper)
        return HandUtils.handleResultByCode(200,  mapOf(
            "total" to queryResult.total,
            "items" to queryResult.records
        ), "获取成功")
    }

    @Transactional
    open fun deleteBanner(bid: Int): Map<String, Any> {
        return if (bannerDao.deleteById(bid) > 0) {
            HandUtils.handleResultByCode(200, null, "删除成功")
        } else HandUtils.handleResultByCode(400, null, "删除失败")
    }

    @Transactional
    open fun updateBanner(bid: Int, name: String, href: String, image: String): Map<String, Any> {
        if (bannerDao.selectOne(QueryWrapper<BannerModel>().eq("bid", bid)) == null) return HandUtils.handleResultByCode(409, null, "轮播不存在")
        val bannerModel = BannerModel()
        ServerBannerManage().updateBanner(bannerModel, bid, name, href, image)
        return if (bannerDao.updateById(bannerModel) > 0) {
            HandUtils.handleResultByCode(200, null, "修改成功")
        } else HandUtils.handleResultByCode(400, null, "修改失败")
    }

    @Transactional
    open fun createBanner(name: String, href: String, image: String): Map<String, Any> {
        val bannerModel = BannerModel()
        ServerBannerManage().setBanner(bannerModel, name, href, image)
        return if (bannerDao.insert(bannerModel) > 0) {
            HandUtils.handleResultByCode(200, null, "创建成功")
        } else HandUtils.handleResultByCode(400, null, "创建失败")
    }
}
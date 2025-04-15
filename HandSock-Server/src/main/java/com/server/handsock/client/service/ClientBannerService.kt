package com.server.handsock.client.service

import com.server.handsock.common.dao.BannerDao
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ClientBannerService @Autowired constructor(private val bannerDao: BannerDao) {
    @Transactional
    open fun searchAllBanner(): Map<String, Any> {
        val bannerList = bannerDao.selectList(null)
        return HandUtils.handleResultByCode(200, bannerList, "获取成功")
    }
}
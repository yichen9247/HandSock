package com.server.handsock.clients.service

import com.server.handsock.clients.dao.ClientBannerDao
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClientBannerService @Autowired constructor(private val clientBannerDao: ClientBannerDao) {
    fun searchAllBanner(): Map<String, Any> {
        try {
            val bannerList = clientBannerDao.selectList(null)
            return HandUtils.handleResultByCode(200, bannerList, "获取成功")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }
}
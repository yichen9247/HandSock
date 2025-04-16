package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.common.dao.ReportDao
import com.server.handsock.common.model.ReportModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ServerReportService @Autowired constructor(private val reportDao: ReportDao) {
     fun getReportList(page: Int, limit: Int): Map<String, Any> {
        val pageObj = Page<ReportModel>(page.toLong(), limit.toLong())
        val wrapper = QueryWrapper<ReportModel>().orderByDesc("time")
        val queryResult = reportDao.selectPage(pageObj, wrapper)
        return HandUtils.handleResultByCode(200,  mapOf(
            "total" to queryResult.total,
            "items" to queryResult.records
        ), "获取成功")
    }

    fun deleteReport(rid: Int): Map<String, Any> {
        return if (reportDao.deleteById(rid) > 0) {
            HandUtils.handleResultByCode(200, null, "删除成功")
        } else HandUtils.handleResultByCode(400, null, "删除失败")
    }
}

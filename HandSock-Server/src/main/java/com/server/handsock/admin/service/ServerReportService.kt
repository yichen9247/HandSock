package com.server.handsock.admin.service

import com.server.handsock.admin.dao.ServerReportDao
import com.server.handsock.admin.mod.ServerReportModel
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class ServerReportService @Autowired constructor(private val serverReportDao: ServerReportDao) {
    fun getReportList(page: Int, limit: Int): Map<String, Any> {
        try {
            val serverReportModelList = serverReportDao.selectList(null)
            val total = serverReportModelList.size
            val startWith = (page - 1) * limit
            serverReportModelList.reverse()
            val endWith = min((startWith + limit).toDouble(), serverReportModelList.size.toDouble()).toInt()
            val subList: List<ServerReportModel?> = serverReportModelList.subList(startWith, endWith)
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

    fun deleteReport(sid: String?): Map<String, Any> {
        return try {
            if (serverReportDao.deleteById(sid) > 0) {
                HandUtils.handleResultByCode(200, null, "删除成功")
            } else HandUtils.handleResultByCode(400, null, "删除失败")
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
        }
    }
}

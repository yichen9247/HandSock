package com.server.handsock.clients.service

import com.server.handsock.clients.dao.ClientReportDao
import com.server.handsock.clients.man.ClientReportManage
import com.server.handsock.clients.mod.ClientReportModel
import com.server.handsock.utils.HandUtils
import com.server.handsock.utils.IDGenerator
import lombok.Getter
import lombok.Setter
import org.springframework.stereotype.Service

@Service @Setter @Getter
class ClientReportService(private val clientReportDao: ClientReportDao) {
    fun addReport(sid: String?, reporterId: Long, reportedId: Long, reason: String?): Map<String, Any> {
        try {
            if (reporterId == reportedId) {
                return HandUtils.handleResultByCode(400, null, "不能举报自己")
            } else {
                val clientReportModel = ClientReportModel()
                ClientReportManage(HandUtils, IDGenerator).insertRepo(clientReportModel, sid, reporterId, reportedId, reason)
                return if (clientReportDao.insert(clientReportModel) > 0) {
                    HandUtils.handleResultByCode(200, null, "举报成功")
                } else HandUtils.handleResultByCode(400, null, "举报失败")
            }
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }
}

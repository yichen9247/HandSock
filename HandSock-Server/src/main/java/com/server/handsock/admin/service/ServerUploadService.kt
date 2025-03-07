package com.server.handsock.admin.service

import com.server.handsock.admin.dao.ServerUploadDao
import com.server.handsock.admin.mod.ServerUploadModel
import com.server.handsock.utils.ConsoleUtils
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import kotlin.math.min

@Service
class ServerUploadService @Autowired constructor(private val serverUploadDao: ServerUploadDao) {
    fun getUploadList(page: Int, limit: Int): Map<String, Any> {
        try {
            val serverUploadModelList = serverUploadDao.selectList(null)
            val startWith = (page - 1) * limit
            val total = serverUploadModelList.size
            serverUploadModelList.reverse()
            val endWith = min((startWith + limit).toDouble(), serverUploadModelList.size.toDouble()).toInt()
            val subList: List<ServerUploadModel?> = serverUploadModelList.subList(startWith, endWith)
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

    fun deleteUpload(fid: String): Map<String, Any> {
        try {
            deleteUploadFile(fid)
            return if (serverUploadDao.deleteById(fid) > 0) {
                HandUtils.handleResultByCode(200, null, "删除成功")
            } else HandUtils.handleResultByCode(400, null, "删除失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    private fun deleteUploadFile(fid: String) {
        try {
            val serverUploadModel = serverUploadDao.selectById(fid)
            val uploadFile = File("upload/${serverUploadModel.type}/${serverUploadModel.path}")
            if (uploadFile.exists() && uploadFile.delete()) {
                ConsoleUtils.printInfoLog("File deleted $fid")
                ConsoleUtils.printInfoLog("$serverUploadModel.path deleted")
            } else ConsoleUtils.printErrorLog(Exception("File delete failed $fid"))
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
        }
    }
}

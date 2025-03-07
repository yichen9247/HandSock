package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.admin.dao.ServerSystemDao
import com.server.handsock.admin.man.ServerSystemManage
import com.server.handsock.admin.mod.ServerSystemModel
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ServerSystemService @Autowired constructor(private val serverSystemDao: ServerSystemDao) {
    fun getSystemKeyStatus(key: String?): Boolean {
        try {
            val serverSystemModel = serverSystemDao.selectOne(QueryWrapper<ServerSystemModel>().eq("name", key))
            if (serverSystemModel != null) return serverSystemModel.value == "open"
            return false
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
            return false
        }
    }

    fun getSystemKeyConfig(key: String?): Map<String, Any> {
        val serverSystemModel = serverSystemDao.selectOne(QueryWrapper<ServerSystemModel>().eq("name", key))
        return HandUtils.handleResultByCode(200, serverSystemModel.value, "获取成功")
    }

    fun setSystemTabooStatus(value: String?): Map<String, Any> {
        try {
            val serverSystemModel = serverSystemDao.selectOne(QueryWrapper<ServerSystemModel>().eq("name", "taboo"))
            val result = ServerSystemManage().setSystemKeyStatus(serverSystemModel, value)
            return if (serverSystemDao.updateById(serverSystemModel) > 0) {
                HandUtils.handleResultByCode(200, result, "设置状态成功")
            } else HandUtils.handleResultByCode(200, null, "设置状态失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun setSystemUploadStatus(value: String?): Map<String, Any> {
        try {
            val serverSystemModel = serverSystemDao.selectOne(QueryWrapper<ServerSystemModel>().eq("name", "upload"))
            val result = ServerSystemManage().setSystemKeyStatus(serverSystemModel, value)
            return if (serverSystemDao.updateById(serverSystemModel) > 0) {
                HandUtils.handleResultByCode(200, result, "设置状态成功")
            } else HandUtils.handleResultByCode(200, null, "设置状态失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun setSystemRegisterStatus(value: String?): Map<String, Any> {
        try {
            val serverSystemModel = serverSystemDao.selectOne(QueryWrapper<ServerSystemModel>().eq("name", "register"))
            val result = ServerSystemManage().setSystemKeyStatus(serverSystemModel, value)
            return if (serverSystemDao.updateById(serverSystemModel) > 0) {
                HandUtils.handleResultByCode(200, result, "设置状态成功")
            } else HandUtils.handleResultByCode(200, null, "设置状态失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun setSystemConfigValue(name: String?, value: String?): Map<String, Any> {
        try {
            val serverSystemModel = serverSystemDao.selectOne(QueryWrapper<ServerSystemModel>().eq("name", name))
                ?: return HandUtils.handleResultByCode(400, null, "未知选项")
            val result = ServerSystemManage().setSystemKeyStatus(serverSystemModel, value)
            return if (serverSystemDao.updateById(serverSystemModel) > 0) {
                HandUtils.handleResultByCode(200, result, "设置成功")
            } else HandUtils.handleResultByCode(200, null, "设置失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun checkAppUpdate(version: String?): Map<String, Any> {
        return try {
            val versionModel = serverSystemDao.selectOne(QueryWrapper<ServerSystemModel>().eq("name", "version"))
            if (version != versionModel.value) {
                val downloadModel = serverSystemDao.selectOne(QueryWrapper<ServerSystemModel>().eq("name", "download"))
                HandUtils.handleResultByCode(201, mapOf(
                    "version" to versionModel.value,
                    "download" to downloadModel.value
                ), "有新版本可更新")
            } else HandUtils.handleResultByCode(200, null, "已是最新版本")
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
        }
    }

    val allSystemConfig: Map<String, Any>
        get() {
            try {
                val serverSystemModelList = serverSystemDao.selectList(null)
                return HandUtils.handleResultByCode(200, serverSystemModelList, "获取成功")
            } catch (e: Exception) {
                return HandUtils.printErrorLog(e)
            }
        }
}

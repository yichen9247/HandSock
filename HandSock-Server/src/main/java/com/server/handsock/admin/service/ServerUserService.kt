package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.admin.dao.ServerChatDao
import com.server.handsock.admin.dao.ServerUserDao
import com.server.handsock.admin.man.ServerUserManage
import com.server.handsock.admin.mod.ServerChatModel
import com.server.handsock.admin.mod.ServerUserModel
import com.server.handsock.clients.dao.ClientUserDao
import com.server.handsock.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class ServerUserService @Autowired constructor(
    private val serverUserDao: ServerUserDao,
    private val serverChatDao: ServerChatDao,
    private val clientUserDao: ClientUserDao
) {

    fun getUserList(page: Int, limit: Int): Map<String, Any> {
        try {
            val serverUserModelList = serverUserDao.selectList(null)
            val startWith = (page - 1) * limit
            serverUserModelList.reverse()
            val endWith = min((startWith + limit).toDouble(), serverUserModelList.size.toDouble()).toInt()
            val subList: List<ServerUserModel?> = serverUserModelList.subList(startWith, endWith)
            return HandUtils.handleResultByCode(200, object : HashMap<Any?, Any?>() {
                init {
                    put("items", subList)
                    put("total", serverUserModelList.size)
                }
            }, "获取成功")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun deleteUser(uid: Long?): Map<String, Any> {
        try {
            val serverChatModel = serverChatDao.selectList(QueryWrapper<ServerChatModel>().eq("uid", uid))
            if (clientUserDao.selectById(uid).isAdmin == 1) return HandUtils.handleResultByCode(409, null, "无法操作管理员账号")
            if (serverChatModel.isEmpty()) {
                if (serverUserDao.deleteById(uid) > 0) return HandUtils.handleResultByCode(200, null, "删除成功")
            } else {
                if (serverUserDao.deleteById(uid) > 0 && serverChatDao.delete(QueryWrapper<ServerChatModel>().eq("uid", uid)) > 0) return HandUtils.handleResultByCode(200, null, "删除成功")
            }
            return HandUtils.handleResultByCode(400, null, "删除失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun updateUserPassword(uid: Long?, password: String?): Map<String, Any> {
        try {
            val serverUserModel = ServerUserModel()
            ServerUserManage(HandUtils).updateUserPassword(serverUserModel, uid, password)
            return if (serverUserDao.updateById(serverUserModel) > 0) {
                HandUtils.handleResultByCode(200, null, "修改成功")
            } else HandUtils.handleResultByCode(400, null, "修改失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun updateUserTabooStatus(uid: Long?, status: String?): Map<String, Any> {
        try {
            val serverUserModel = ServerUserModel()
            ServerUserManage(HandUtils).updateUserTabooStatus(serverUserModel, uid, status)
            return if (serverUserDao.updateById(serverUserModel) > 0) {
                HandUtils.handleResultByCode(200, null, "设置成功")
            } else HandUtils.handleResultByCode(400, null, "设置失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun updateUserInfo(uid: Long?, username: String?, nick: String?, avatar: String?, robot: Boolean): Map<String, Any> {
        try {
            if (serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("uid", uid)) == null) return HandUtils.handleResultByCode(409, null, "用户不存在")
            val serverUserModel = ServerUserModel()
            val robotModel = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("is_robot", 1))
            if (robot) {
                if (robotModel != null) {
                    robotModel.isRobot = 0
                    ServerUserManage(HandUtils).updateUserInfo(serverUserModel, uid, username, nick, avatar, 1)
                    if (serverUserDao.updateById(robotModel) > 0 && serverUserDao.updateById(serverUserModel) > 0) return HandUtils.handleResultByCode(200, null, "修改成功")
                } else {
                    ServerUserManage(HandUtils).updateUserInfo(serverUserModel, uid, username, nick, avatar, 1)
                    if (serverUserDao.updateById(serverUserModel) > 0) return HandUtils.handleResultByCode(200, null, "修改成功")
                }
            } else {
                ServerUserManage(HandUtils).updateUserInfo(serverUserModel, uid, username, nick, avatar, 0)
                if (serverUserDao.updateById(serverUserModel) > 0) return HandUtils.handleResultByCode(200, null, "修改成功")
            }
            return HandUtils.handleResultByCode(400, null, "修改失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun getUserInfo(uid: Long): ServerUserModel { // 谨慎使用此方法
        return serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("uid", uid))
    }
}

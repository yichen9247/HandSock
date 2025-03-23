package com.server.handsock.clients.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.admin.dao.ServerUserDao
import com.server.handsock.admin.mod.ServerUserModel
import com.server.handsock.admin.service.ServerUserService
import com.server.handsock.clients.dao.ClientUserDao
import com.server.handsock.clients.man.ClientUserManage
import com.server.handsock.clients.mod.ClientUserModel
import com.server.handsock.services.TokenService
import com.server.handsock.utils.ConsoleUtils
import com.server.handsock.utils.GlobalService.socketIOServer
import com.server.handsock.utils.HandUtils
import com.server.handsock.utils.IDGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ClientUserService @Autowired constructor(
    private val tokenService: TokenService,
    private val serverUserDao: ServerUserDao,
    private val clientUserDao: ClientUserDao,
    private val serverUserService: ServerUserService,
) {
    fun queryUserInfo(uid: Long): ClientUserModel {
        return clientUserDao.selectById(uid)
    }

    fun queryAllUser(): Map<String, Any> {
        try {
            return HandUtils.handleResultByCode(200, clientUserDao.selectList(null), "获取成功")
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
            return HandUtils.handleResultByCode(500, null, "服务端异常")
        }
    }

    fun checkUserLogin(uid: Long): Boolean {
        try {
            return clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("uid", uid)) != null
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
            return false
        }
    }

    fun getUserInnerStatus(uid: Long?): Boolean {
        try {
            val selectResult = clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("uid", uid))
            return selectResult != null
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
            return false
        }
    }

    fun getUserTabooStatus(uid: Long?): Boolean {
        try {
            val status = clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("uid", uid)).taboo
            return status == "open"
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
            return false
        }
    }

    val robotInnerStatus: ClientUserModel?
        get() {
            try {
                val selectResult = clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("is_robot", 1))
                return selectResult
            } catch (e: Exception) {
                HandUtils.printErrorLog(e)
                return null
            }
        }

    fun getUserInfo(uid: Long?): Map<String, Any> {
        try {
            val selectResult = clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("uid", uid))
            return if (selectResult != null) {
                HandUtils.handleResultByCode(200, selectResult, "获取成功")
            } else HandUtils.handleResultByCode(404, null, "用户不存在")
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
            return HandUtils.handleResultByCode(500, null, "服务端异常")
        }
    }

    fun setUserAiAuthorization(uid: Long?, status: Boolean): Boolean {
        val clientUserModel = ClientUserModel()
        try {
            clientUserModel.uid = uid
            clientUserModel.aiAuth = if (status) 1 else 0
            return clientUserDao.updateById(clientUserModel) > 0
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
            return false
        }
    }

    fun getUserAdminStatusByUid(uid: Long?): Boolean {
        try {
            val selectResult = clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("uid", uid))
            return if (selectResult != null) {
                selectResult.isAdmin == 1
            } else false
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
            return false
        }
    }

    fun loginUser(username: String, password: String, address: String): Map<String, Any> {
        if (HandUtils.isValidUsername(username) || HandUtils.isValidPassword(password)) return HandUtils.handleResultByCode(400, null, "输入格式不合规")
        try {
            val selectResult = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("username", username))
            return if (selectResult == null || selectResult.password != HandUtils.encodeStringToMD5(password)) {
                HandUtils.handleResultByCode(409, null, "账号或密码错误")
            } else {
                userLogin(
                    address = address,
                    username = username,
                    selectResult = selectResult
                )
            }
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun loginUserScan(username: String, address: String): Map<String, Any> {
        try {
            val selectResult = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("username", username))
            return if (selectResult == null) {
                HandUtils.handleResultByCode(409, null, "未查询到用户")
            } else {
                userLogin(
                    address = address,
                    username = username,
                    selectResult = selectResult
                )
            }
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun registerUser(username: String, password: String, address: String?): Map<String, Any> {
        if (HandUtils.isValidUsername(username) || HandUtils.isValidPassword(password)) return HandUtils.handleResultByCode(400, null, "输入格式不合规")
        if (serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("username", username)) != null) {
            return HandUtils.handleResultByCode(409, null, "用户名已存在")
        } else {
            try {
                val uid = generateUniqueUid()
                val serverUserModel = ServerUserModel()
                val result = ClientUserManage().registerUser(serverUserModel, uid, username, HandUtils.encodeStringToMD5(password), address)
                val userinfo = result["userinfo"] as Map<*, *>?
                result["token"] =
                    tokenService.generateUserToken(userinfo!!["uid"].toString().toLong(), username, address!!)
                val token = result["token"]
                if (serverUserDao.insert(serverUserModel) > 0) {
                    ConsoleUtils.printInfoLog("User Register $address $uid $token")
                    HandUtils.sendGlobalMessage(socketIOServer!!, "[RE:USER:ALL]", null)
                    return HandUtils.handleResultByCode(200, result, "注册成功")
                } else return HandUtils.handleResultByCode(407, null, "注册失败")
            } catch (e: Exception) {
                return HandUtils.printErrorLog(e)
            }
        }
    }

    fun editForNick(uid: Long, nick: String): Map<String, Any> {
        if (nick.length > 10 || nick.length < 2) return HandUtils.handleResultByCode(400, null, "昵称长度不合规")
        try {
            val serverUserModel = ServerUserModel()
            val result = ClientUserManage().updateNick(serverUserModel, uid, nick)
            if (serverUserDao.updateById(serverUserModel) > 0) {
                HandUtils.sendGlobalMessage(
                    event = "[RE:USER:NICK]",
                    server = socketIOServer!!,
                    content = HandUtils.handleResultByCode(200, result, "修改昵称成功")
                )
                return HandUtils.handleResultByCode(200, result, "修改昵称成功")
            } else return HandUtils.handleResultByCode(400, result, "修改昵称失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun editForAvatar(uid: Long, path: String?): Map<String, Any> {
        try {
            val serverUserModel = ServerUserModel()
            val result = ClientUserManage().updateAvatar(serverUserModel, uid, path)
            if (serverUserDao.updateById(serverUserModel) > 0) {
                HandUtils.sendGlobalMessage(
                    event = "[RE:USER:AVATAR]",
                    server = socketIOServer!!,
                    content = HandUtils.handleResultByCode(200, result, "修改头像成功")
                )
                return HandUtils.handleResultByCode(200, result, "修改头像成功")
            } else return HandUtils.handleResultByCode(400, result, "修改头像失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun editForUserName(uid: Long, username: String): Map<String, Any> {
        if (HandUtils.isValidUsername(username)) return HandUtils.handleResultByCode(400, null, "账号格式不合规")
        try {
            val serverUserModel = ServerUserModel()
            val result = ClientUserManage().updateUserName(serverUserModel, uid, username)
            if (serverUserDao.updateById(serverUserModel) > 0) {
                HandUtils.sendGlobalMessage(
                    event = "[RE:USER:USERNAME]",
                    server = socketIOServer!!,
                    content = HandUtils.handleResultByCode(200, result, "修改用户名成功")
                )
                return HandUtils.handleResultByCode(200, result, "修改账号成功")
            } else return HandUtils.handleResultByCode(400, result, "修改账号失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun editForPassword(uid: Long, password: String): Map<String, Any> {
        if (HandUtils.isValidPassword(password)) return HandUtils.handleResultByCode(400, null, "密码格式不合规")
        try {
            val serverUserModel = ServerUserModel()
            ClientUserManage().updatePassword(serverUserModel, uid, HandUtils.encodeStringToMD5(password))
            return if (serverUserDao.updateById(serverUserModel) > 0) {
                HandUtils.handleResultByCode(200, null, "修改密码成功")
            } else HandUtils.handleResultByCode(400, null, "修改密码失败")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    fun getUserQrcodeScanStatus(qid: String, address: String): Map<String, Any> {
        try {
            val status = tokenService.getScanStatus(qid)
                ?: return HandUtils.handleResultByCode(401, null, "二维码已过期")
            return when(status) {
                "0" -> HandUtils.handleResultByCode(400, null, "等待扫码中")
                "1" -> {
                    val targetUser = tokenService.getScanTargetUser(qid)
                        ?: return HandUtils.handleResultByCode(500, null, "服务器异常")
                    val user = serverUserService.getUserInfo(targetUser.toLong())
                    tokenService.removeScanStatus(qid)
                    loginUserScan(
                        address = address,
                        username = user.username,
                    )
                }
                else -> HandUtils.handleResultByCode(500, null, "服务器异常")
            }
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    private fun generateUniqueUid(): Long {
        var uid: Long
        do {
            uid = IDGenerator.generateRandomId(8)
        } while (serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("uid", uid)) != null)
        return uid
    }

    private fun userLogin(selectResult: ServerUserModel, username: String, address: String): Map<String, Any> {
        val uid = selectResult.uid
        val token = tokenService.generateUserToken(selectResult.uid, username, address)
        ConsoleUtils.printInfoLog("User Login $address $uid $token")
        return HandUtils.handleResultByCode(200, java.util.Map.of("token", token, "userinfo", clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("username", username))), "登录成功")
    }
}

package com.server.handsock.clients.controller

import com.server.handsock.clients.service.ClientUserService
import com.server.handsock.services.AuthService
import com.server.handsock.services.TokenService
import com.server.handsock.utils.HandUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class ClientUserController @Autowired constructor(
    private val authService: AuthService,
    private val tokenService: TokenService,
    private val clientUserService: ClientUserService
) {
    @PostMapping("/login")
    fun userLogin(@RequestBody data: Map<String?, Any>, request: HttpServletRequest): Map<String, Any> {
        try {
            val username = data["username"]?.toString()
            val password = data["password"]?.toString()
            val androidId = request.getHeader("androidId")
            return if (username == null || password == null || androidId == null) {
                HandUtils.handleResultByCode(403, null, "参数错误")
            } else clientUserService.loginUser(username, password, androidId)
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    @PostMapping("/register")
    fun userRegister(@RequestBody data: Map<String?, Any>, request: HttpServletRequest): Map<String, Any> {
        try {
            val username = data["username"]?.toString()
            val password = data["password"]?.toString()
            val androidId = request.getHeader("androidId")
            return if (username == null || password == null || androidId == null) {
                HandUtils.handleResultByCode(403, null, "参数错误")
            } else clientUserService.registerUser(username, password, androidId)
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    @GetMapping("/info")
    fun getUserInfo(request: HttpServletRequest, @RequestParam("uid") uid: Long?): Any {
        return authService.validClientStatusByRequest(request) {
            clientUserService.getUserInfo(uid)
        }
    }

    @PostMapping("/edit/avatar")
    fun editAvatar(@RequestBody data: Map<String?, Any>, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            val path = data["path"].toString()
            val uid = data["uid"].toString().toLong()
            clientUserService.editForAvatar(uid, path)
        }
    }

    @PostMapping("/edit/nick")
    fun editNick(@RequestBody data: Map<String?, Any>, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            val uid = data["uid"].toString().toLong()
            val nick = data["nick"].toString()
            clientUserService.editForNick(uid, nick)
        }
    }

    @PostMapping("/edit/username")
    fun editUserName(@RequestBody data: Map<String?, Any>, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            val uid = data["uid"].toString().toLong()
            val username = data["username"].toString()
            clientUserService.editForUserName(uid, username)
        }
    }

    @PostMapping("/edit/password")
    fun editPassword(@RequestBody data: Map<String?, Any>, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            val uid = data["uid"].toString().toLong()
            val password = data["password"].toString()
            clientUserService.editForPassword(uid, password)
        }
    }

    @PostMapping("/scan/login")
    fun userScanLogin(@RequestBody data: Map<String?, Any>, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            try {
                val uid = data["uid"].toString()
                val qid = data["qid"].toString()
                val status = data["status"].toString().toInt()

                when(status) {
                    0 -> {
                        tokenService.removeScanStatus(qid)
                        HandUtils.handleResultByCode(200, null, "已取消登录")
                    }
                    1 -> {
                        tokenService.setScanStatus(qid, 1)
                        tokenService.setScanTargetUser(
                            qid = qid,
                            uid = uid
                        )
                        HandUtils.handleResultByCode(200, null, "登录成功")
                    }
                    else -> HandUtils.handleResultByCode(400, null, "未知参数")
                }
            } catch (e: Exception) {
                HandUtils.printErrorLog(e)
            }
        }
    }

    @PostMapping("/scan/status")
    fun getQrcodeScanStatus(@RequestBody data: Map<String?, Any>, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            try {
                val qid = data["qid"].toString()
                val androidId = "Android Check"
                clientUserService.getUserQrcodeScanStatus(
                    qid = qid,
                    address = androidId
                )
            } catch (e: Exception) {
                HandUtils.printErrorLog(e)
            }
        }
    }
}

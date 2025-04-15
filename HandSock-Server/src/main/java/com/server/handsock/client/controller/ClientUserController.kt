package com.server.handsock.client.controller

import com.server.handsock.client.service.ClientUserService
import com.server.handsock.common.data.ControllerScanLoginModel
import com.server.handsock.common.data.ControllerScanLoginStatus
import com.server.handsock.common.data.ControllerUserBind
import com.server.handsock.common.data.ControllerUserUpdate
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.service.AuthService
import com.server.handsock.service.TokenService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/android/user")
class ClientUserController @Autowired constructor(
    private val authService: AuthService,
    private val tokenService: TokenService,
    private val clientUserService: ClientUserService
) {
    @PostMapping("/login")
    fun userLogin(@RequestBody data: ControllerUserUpdate, request: HttpServletRequest): Map<String, Any> {
        try {
            val androidId = request.getHeader("androidId")
            return if (data.username.isNullOrEmpty() || data.password.isNullOrEmpty() || androidId.isNullOrEmpty()) {
                HandUtils.printParamError()
            } else clientUserService.loginUser(
                address = androidId,
                username = data.username,
                password = data.password
            )
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    @PostMapping("/login/qq")
    fun userLogin(@RequestBody data: ControllerUserBind, request: HttpServletRequest): Map<String, Any> {
        try {
            val androidId = request.getHeader("androidId")
            return if (data.qqId.isNullOrEmpty()) {
                HandUtils.printParamError()
            } else clientUserService.loginUserWithQQ(
                qqId = data.qqId,
                address = androidId,
            )
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    @PostMapping("/register")
    fun userRegister(@RequestBody data: ControllerUserUpdate, request: HttpServletRequest): Map<String, Any> {
        try {
            val androidId = request.getHeader("androidId")
            return if (data.username.isNullOrEmpty() || data.password.isNullOrEmpty() || androidId.isNullOrEmpty()) {
                HandUtils.printParamError()
            } else clientUserService.registerUser(
                address = androidId,
                username = data.username,
                password = data.password
            )
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    @GetMapping("/info")
    fun getUserInfo(request: HttpServletRequest, @RequestParam("uid") uid: Long?): Any {
        return authService.validClientStatusByRequest(request) {
            if (uid == null || uid <= 0) {
                HandUtils.printParamError()
            } else clientUserService.getUserInfo(uid)
        }
    }

    @GetMapping("/bind")
    fun getUserBind(request: HttpServletRequest, @RequestParam("uid") uid: Long?): Any {
        return authService.validClientStatusByRequest(request) {
            if (uid == null || uid <= 0) {
                HandUtils.printParamError()
            } else clientUserService.getUserBind(uid)
        }
    }

    @PostMapping("/bind/qq")
    fun setUserQQBind(@RequestBody data: ControllerUserBind, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            val uid = request.getHeader("uid")?.toLong()
            if (uid == null || uid <= 0 || data.qqId.isNullOrEmpty()) {
                HandUtils.printParamError()
            } else clientUserService.bindUserWithQQ(
                uid = uid,
                qqId = data.qqId
            )
        }
    }

    @PostMapping("/edit/avatar")
    fun editAvatar(@RequestBody data: ControllerUserUpdate, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            if (data.path.isNullOrEmpty() || data.uid == null || data.uid <= 0) {
                HandUtils.printParamError()
            } else clientUserService.editForAvatar(
                uid = data.uid,
                path = data.path
            )
        }
    }

    @PostMapping("/edit/nick")
    fun editNick(@RequestBody data: ControllerUserUpdate, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            if (data.nick.isNullOrEmpty() || data.uid == null || data.uid <= 0) {
                HandUtils.printParamError()
            } else clientUserService.editForNick(
                uid = data.uid,
                nick = data.nick
            )
        }
    }

    @PostMapping("/edit/username")
    fun editUserName(@RequestBody data: ControllerUserUpdate, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            if (data.username.isNullOrEmpty() || data.uid == null || data.uid <= 0) {
                HandUtils.printParamError()
            } else clientUserService.editForUserName(
                uid = data.uid,
                username = data.username
            )
        }
    }

    @PostMapping("/edit/password")
    fun editPassword(@RequestBody data: ControllerUserUpdate, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            if (data.password.isNullOrEmpty() || data.uid == null || data.uid <= 0) {
                HandUtils.printParamError()
            } else clientUserService.editForPassword(
                uid = data.uid,
                password = data.password
            )
        }
    }

    @PostMapping("/login/scan")
    fun userScanLogin(@RequestBody data: ControllerScanLoginModel, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            try {
                if (data.uid != null && data.qid != null && data.status != null && data.uid.isNotEmpty()) {
                    when(data.status) {
                        0 -> {
                            tokenService.removeScanStatus(data.qid)
                            HandUtils.handleResultByCode(200, null, "已取消登录")
                        }
                        1 -> {
                            tokenService.setScanStatus(data.qid, 1)
                            tokenService.setScanTargetUser(
                                qid = data.qid,
                                uid = data.uid
                            )
                            HandUtils.handleResultByCode(200, null, "登录成功")
                        }
                        else -> HandUtils.handleResultByCode(400, null, "未知参数")
                    }
                } else HandUtils.printParamError()
            } catch (e: Exception) {
                HandUtils.printErrorLog(e)
            }
        }
    }

    @PostMapping("/login/scan/status")
    fun getQrcodeScanStatus(@RequestBody data: ControllerScanLoginStatus, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            try {
                if (data.qid.isNullOrEmpty()) {
                    HandUtils.printParamError()
                } else {
                    val androidId = "Android Check"
                    clientUserService.getUserQrcodeScanStatus(
                        qid = data.qid,
                        address = androidId
                    )
                }
            } catch (e: Exception) {
                HandUtils.printErrorLog(e)
            }
        }
    }
}

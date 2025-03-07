package com.server.handsock.admin.controller

import com.server.handsock.admin.service.ServerUserService
import com.server.handsock.services.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/user")
class ServerUserController @Autowired constructor(
    private val authService: AuthService,
    private val serverUserService: ServerUserService
) {
    @PostMapping("/set/taboo")
    fun updateUserTabooStatus(request: HttpServletRequest, @RequestBody data: Map<String?, Any>): Any {
        return authService.validAdminStatusByRequest(request) {
            val status = data["status"].toString()
            val uid = data["uid"].toString().toLong()
            serverUserService.updateUserTabooStatus(uid, status)
        }
    }

    @PostMapping("/set/info")
    fun updateUserInfo(request: HttpServletRequest, @RequestBody data: Map<String?, Any>): Any {
        return authService.validAdminStatusByRequest(request) {
            val nick = data["nick"].toString()
            val avatar = data["avatar"].toString()
            val uid = data["uid"].toString().toLong()
            val username = data["username"].toString()
            val robot = data["robot"].toString().toBoolean()
            serverUserService.updateUserInfo(uid, username, nick, avatar, robot)
        }
    }
}

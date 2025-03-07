package com.server.handsock.clients.controller

import com.server.handsock.admin.service.ServerSystemService
import com.server.handsock.services.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/android/system")
class AndroidController @Autowired constructor(
    private val authService: AuthService,
    private val serverSystemService: ServerSystemService
) {
    @PostMapping("/update")
    fun checkAppUpdate(@RequestBody data: Map<String?, Any>, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            val version = data["version"].toString()
            serverSystemService.checkAppUpdate(version)
        }
    }
}
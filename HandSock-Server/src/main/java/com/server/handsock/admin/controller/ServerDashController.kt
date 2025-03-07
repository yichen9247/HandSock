package com.server.handsock.admin.controller

import com.server.handsock.admin.service.ServerDashService
import com.server.handsock.services.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/dash")
class ServerDashController @Autowired constructor(
    private val authService: AuthService,
    private val serverDashService: ServerDashService
) {
    @GetMapping("/data")
    fun getDashboardData(request: HttpServletRequest): Any {
        return authService.validAdminStatusByRequest(request) {
            serverDashService.dashboardData
        }
    }
}

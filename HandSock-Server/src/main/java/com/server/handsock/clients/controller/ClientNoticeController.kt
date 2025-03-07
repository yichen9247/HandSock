package com.server.handsock.clients.controller

import com.server.handsock.clients.service.ClientNoticeService
import com.server.handsock.services.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/android/notice")
class ClientNoticeController @Autowired constructor(
    private val authService: AuthService,
    private val clientNoticeService: ClientNoticeService
) {
    @GetMapping("/search/all")
    fun searchAllNotice(request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            clientNoticeService.searchAllNotice()
        }
    }
}
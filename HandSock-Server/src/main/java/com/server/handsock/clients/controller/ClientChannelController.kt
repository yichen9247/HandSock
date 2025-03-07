package com.server.handsock.clients.controller

import com.server.handsock.clients.service.ClientChannelService
import com.server.handsock.services.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/android/channel")
class ClientChannelController @Autowired constructor(
    private val authService: AuthService,
    private val clientChannelService: ClientChannelService
) {
    @GetMapping("/search/all")
    fun searchAllGroup(request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            clientChannelService.searchAllGroup()
        }
    }

    @PostMapping("/search/gid")
    fun searchGroupByGid(request: HttpServletRequest, @RequestBody data: Map<String?, Any>): Any {
        return authService.validClientStatusByRequest(request) {
            clientChannelService.searchGroupByGid(data["gid"].toString().toLong())
        }
    }
}

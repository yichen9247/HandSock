package com.server.handsock.client.controller

import com.server.handsock.admin.service.ServerSystemService
import com.server.handsock.common.data.CommonCheckVersion
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.service.AuthService
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
    fun checkAppUpdate(@RequestBody data: CommonCheckVersion, request: HttpServletRequest): Any {
        return authService.validClientStatusByRequest(request) {
            if (data.version.isNullOrEmpty()) {
                HandUtils.printParamError()
            } else serverSystemService.checkAppUpdate(data.version)
        }
    }
}
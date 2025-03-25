package com.server.handsock.api.open

import com.server.handsock.clients.service.ClientChatService
import com.server.handsock.clients.service.ClientUserService
import com.server.handsock.services.AuthService
import com.server.handsock.services.ClientService
import com.server.handsock.utils.HandUtils
import com.server.handsock.utils.RobotUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/openapi")
open class OpenAPIController @Autowired constructor(
    private val authService: AuthService,
    private val clientService: ClientService,
    private val clientUserService: ClientUserService,
    private val clientChatService: ClientChatService
) {
    @PostMapping("/robot/send")
    fun sendRobotMessage(@RequestBody data: Map<String?, Any>, request: HttpServletRequest): Any {
        return authService.validOpenApiRequestLimit("Robot-Send", request) {
            if (data["gid"] == null || data["content"] == null) {
                HandUtils.handleResultByCode(400, null, "必填参数不为空")
            } else {
                val gid = data["gid"].toString()
                val content = data["content"].toString()

                RobotUtils.sendRobotMessage(
                    content = content,
                    gid = gid.toLong(),
                    address = request.remoteAddr,
                    clientService = clientService,
                    clientUserService = clientUserService,
                    clientChatService = clientChatService
                )
            }
        }
    }
}
package com.server.handsock.client.controller

import com.server.handsock.client.service.ClientCommentService
import com.server.handsock.common.data.ControllerAddForumComment
import com.server.handsock.common.service.CommentService
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/android/comment")
class ClientCommentController @Autowired constructor(
    private val authService: AuthService,
    private val commentService: CommentService,
    private val clientCommentService: ClientCommentService
) {
    @GetMapping("/search/all")
    fun searchAllForumComment(request: HttpServletRequest, @RequestParam("pid") pid: Int?): Any {
        return authService.validClientStatusByRequest(request) {
            if (pid == null || pid <= 0) {
                HandUtils.printParamError()
            } else commentService.searchPostComment(pid = pid, 1, 25)
        }
    }

    @PostMapping("/add")
    fun addForumComment(request: HttpServletRequest, @RequestBody data: ControllerAddForumComment): Any {
        return authService.validClientStatusByRequest(request) {
            val uid = request.getHeader("uid")?.toLong()
            if (data.parent != null && data.parent <= 0) HandUtils.printParamError()
            if (uid == null || uid <= 0 || data.pid == null || data.pid <= 0 || data.content.isNullOrEmpty() || data.content.trim().isEmpty()) {
                HandUtils.printParamError()
            } else clientCommentService.addPostComment(
                uid = uid,
                pid = data.pid,
                parent = data.parent,
                content = data.content
            )
        }
    }
}
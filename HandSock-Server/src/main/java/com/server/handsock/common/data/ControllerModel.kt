package com.server.handsock.common.data

data class ControllerUserUpdate(
    val uid: Long? = null,
    val path: String? = null,
    val nick: String? = null,
    val username: String? = null,
    val password: String? = null
)

data class ControllerUserBind(
    val qqId: String? = null
)

data class ControllerScanLoginModel(
    val uid: String? = null,
    val qid: String? = null,
    val status: Int? = null
)

data class ControllerAddForumPost(
    val title: String? = null,
    val image: String? = null,
    val content: String? = null
)

data class ControllerAddForumComment(
    val pid: Int? = null,
    val parent: Int? = null,
    val content: String? = null
)

data class ControllerSearchChannel(
    val gid: Long? = null
)

data class ControllerScanLoginStatus(
    val qid: String? = null
)
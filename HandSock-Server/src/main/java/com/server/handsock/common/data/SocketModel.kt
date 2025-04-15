package com.server.handsock.common.data

data class SocketAddForumPost(
    val title: String? = null,
    val image: String? = null,
    val content: String? = null
)

data class SocketAddPostComment(
    val pid: Int? = null,
    val parent: Int? = null,
    val content: String? = null
)

data class SocketSearchChannel(
    val gid: Long? = null
)

data class SocketUserReport(
    val sid: String? = null,
    val reason: String? = null,
    val reportedId: Long? = null
)

data class SocketSystemConfig(
    val name: String? = null,
    val value: String? = null
)

data class SocketUserUpdate(
    val nick: String? = null,
    val path: String? = null,
    val username: String? = null,
    val password: String? = null
)

data class SocketUserMessage(
    val type: String? = null,
    val content: String? = null
)
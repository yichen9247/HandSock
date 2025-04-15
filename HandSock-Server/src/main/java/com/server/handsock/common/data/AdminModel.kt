package com.server.handsock.common.data

data class UserMannerModel(
    val uid: Long?,
    val status: Int?,
    val nick: String?,
    val robot: Boolean?,
    val avatar: String?,
    val username: String?,
    val password: String?
)

data class AdminMannerModel(
    val pid: Int? = null,
    val yid: Int? = null,
    val bid: Int? = null,
    val rid: Int? = null,
    val uid: Long? = null,
    val nid: Int? = null,
    val cid: Int? = null,
    val gid: Long? = null,
    val fid: String? = null,
    val sid: String? = null,
    val status: Any? = null,
    val name: String? = null,
    val title: String? = null,
    val image: String? = null,
    val avatar: String? = null,
    val content: String? = null,

    // User
    val nick: String? = null,
    val robot: Boolean? = null,
    val username: String? = null,
    val password: String? = null,

    // Channel
    val notice: String? = null,
    val aiRole: Boolean? = null,

    // Banner
    val href: String? = null,

    // Search
    val page: Int? = null,
    val limit: Int? = null
)
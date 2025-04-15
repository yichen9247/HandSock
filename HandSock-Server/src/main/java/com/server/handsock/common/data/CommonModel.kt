package com.server.handsock.common.data

data class CommonSearchPage(
    val pid: Int? = null,
    val page: Int? = null,
    val limit: Int? = null
)

data class CommonCheckVersion(
    val version: String? = null
)
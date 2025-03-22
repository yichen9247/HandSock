package com.server.handsock.utils

import java.util.*

object IDGenerator {
    fun generateUniqueId(): String {
        return UUID.randomUUID().toString()
    }

    fun generateRandomId(length: Int): Long {
        val currentYear = Calendar.getInstance()[Calendar.YEAR]
        val yearPrefix = currentYear.toString().substring(if (currentYear - 2000 > 0) 2 else 0)
        val random = Random()
        val randomSuffix = String.format("%0" + length + "d", random.nextInt(1000000))
        return (yearPrefix + randomSuffix).toLong()
    }

    fun generateRandomMessageId(uid: Long, gid: Long, address: String): String {
        return HandUtils.encodeStringToMD5(
            UUID.randomUUID().toString() + "-" + HandUtils.encodeStringToMD5(
                uid.toString() + gid.toString() + address
            )
        )
    }

    fun generateRandomReportedId(sid: String, reporterId: Long, reportedId: Long): String {
        return HandUtils.encodeStringToMD5(
            UUID.randomUUID().toString() + "-" + HandUtils.encodeStringToMD5(
                sid + reporterId + reportedId
            )
        )
    }

    fun generateRandomFileId(uid: Long, name: String, path: String, time: String): String {
        return HandUtils.encodeStringToMD5(
            UUID.randomUUID().toString() + "-" + HandUtils.encodeStringToMD5(
                uid.toString() + name + path + time
            )
        )
    }
}
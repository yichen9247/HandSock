package com.server.handsock.utils

import org.slf4j.LoggerFactory

object ConsoleUtils {
    private val logger = LoggerFactory.getLogger("HandSock-Server")

    fun printErrorLog(content: Any) {
        logger.error(content.toString())
    }

    fun printInfoLog(content: Any) {
        logger.info(content.toString())
    }

    fun printWarnLog(content: Any) {
        logger.warn(content.toString())
    }

    fun printSuccessLog(content: Any) {
        logger.info(content.toString())
    }
}
package com.server.handsock

import com.server.handsock.checks.ServerChecker
import com.server.handsock.utils.ConsoleUtils
import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import kotlin.system.exitProcess

@SpringBootApplication
@MapperScan("com.server.handsock")
open class HandSock(
    private val serverChecker: ServerChecker
) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                System.setProperty("spring.devtools.restart.enabled", "false")
                val context = SpringApplication.run(HandSock::class.java, *args)
                ConsoleUtils.printSuccessLog("Springboot server started!")
                context.getBean(HandSock::class.java).serverChecker.checkMysqlConnection()
                context.getBean(HandSock::class.java).serverChecker.checkRedisConnection()
                context.getBean(HandSock::class.java).serverChecker.checkDatabaseTable()
            } catch (e: Exception) {
                ConsoleUtils.printErrorLog(e)
                exitProcess(0)
            }
        }
    }
}
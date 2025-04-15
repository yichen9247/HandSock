package com.server.handsock

import com.corundumstudio.socketio.SocketIOServer
import com.server.handsock.common.utils.ConsoleUtils
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class ServerRunner(
    private val socketIOServer: SocketIOServer
) : CommandLineRunner {
    override fun run(vararg args: String) {
        try {
            socketIOServer.start()
            ConsoleUtils.printSuccessLog("Socket.IO server started!")
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
        }
    }
}
package com.server.handsock.utils

import com.corundumstudio.socketio.SocketIOServer
import lombok.Setter
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Setter @Component @Service
object GlobalService {
    var socketIOServer: SocketIOServer? = null
}
package com.server.handsock.common.utils

import com.corundumstudio.socketio.SocketIOServer
import com.server.handsock.common.props.HandProp
import lombok.Setter
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Setter @Component @Service
object GlobalService {
    var handsockProps: HandProp? = null
    var socketIOServer: SocketIOServer? = null
}
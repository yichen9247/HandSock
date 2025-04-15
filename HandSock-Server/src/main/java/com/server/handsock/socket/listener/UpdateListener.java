package com.server.handsock.socket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.common.data.SocketUserUpdate;
import com.server.handsock.common.utils.GlobalService;
import com.server.handsock.socket.handler.UpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateListener {

    private final SocketIOServer server;
    private final UpdateHandler updateHandler;

    @Autowired
    public UpdateListener(UpdateHandler updateHandler) {
        this.server = GlobalService.INSTANCE.getSocketIOServer();
        this.updateHandler = updateHandler;
    }

    public void addEventListener() {
        server.addEventListener("[EDIT:USER:NICK]", SocketUserUpdate.class, (client, data, ackSender) -> {
            updateHandler.handleEditUserInfo(client, data, ackSender, "USER:NICK");
        });

        server.addEventListener("[EDIT:USER:AVATAR]", SocketUserUpdate.class, (client, data, ackSender) -> {
            updateHandler.handleEditUserInfo(client, data, ackSender, "USER:AVATAR");
        });

        server.addEventListener("[EDIT:USER:USERNAME]", SocketUserUpdate.class, (client, data, ackSender) -> {
            updateHandler.handleEditUserInfo(client, data, ackSender, "USER:USERNAME");
        });

        server.addEventListener("[EDIT:USER:PASSWORD]", SocketUserUpdate.class, (client, data, ackSender) -> {
            updateHandler.handleEditUserInfo(client, data, ackSender, "USER:PASSWORD");
        });
    }
}

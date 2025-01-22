package com.server.handsock.console;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.BroadcastOperations;

public class MessageUtils {
    public void sendGlobalMessage(SocketIOServer server, String event, Object content) {
        BroadcastOperations broadcastOperations = server.getBroadcastOperations();
        broadcastOperations.sendEvent(event, content);
    }
}

package com.server.handsock.websocket.eventer;

import com.server.handsock.console.MD5Encoder;
import org.springframework.stereotype.Service;
import com.server.handsock.console.MessageUtils;
import com.server.handsock.console.ConsolePrints;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.SocketIOClient;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

@Service
public class OnlineEvent {

    private final ConsolePrints consolePrints = new ConsolePrints();
    public final List<String> clientList = Collections.synchronizedList(new ArrayList<>());
    public final List<Map<String, Object>> userList = Collections.synchronizedList(new ArrayList<>());

    private String getClientUUID(SocketIOClient client) {
        String sessionId = client.getSessionId().toString();
        return new MD5Encoder().toMD5(sessionId);
    }

    private String getUserPlatform(SocketIOClient client) {
        String userAgent = client.getHandshakeData().getHttpHeaders().get("User-Agent");
        return userAgent != null ? userAgent.split(" ")[0] : "Unknown";
    }

    public void sendUserConnect(SocketIOServer server, SocketIOClient client, String address) {
        String uuid = getClientUUID(client);
        if (!clientList.contains(uuid)) {
            synchronized (clientList) {
                clientList.add(uuid);
            }

            synchronized (userList) {
                userList.add(Map.of("uuid", uuid, "platform", getUserPlatform(client)));
            }

            client.sendEvent("[TOKENS]", Map.of("data", uuid));
            sendMessage(server);
            logConnectionEvent(address, uuid, "New visitor");
        }
    }

    public void sendUserDisconnect(SocketIOServer server, SocketIOClient client, String address) {
        String uuid = getClientUUID(client);
        if (clientList.contains(uuid)) {
            synchronized (clientList) {
                clientList.remove(uuid);
            }

            synchronized (userList) {
                userList.removeIf(map -> map.get("uuid").equals(uuid));
            }

            sendMessage(server);
            logConnectionEvent(address, uuid, "New visitor leave");
        }
    }

    private void sendMessage(SocketIOServer server) {
        new MessageUtils().sendGlobalMessage(server, "[ONLINE]", Map.of("data", userList));
    }

    private void logConnectionEvent(String address, String uuid, String event) {
        consolePrints.printInfoLogV1(event + " " + address + " " + uuid);
    }
}

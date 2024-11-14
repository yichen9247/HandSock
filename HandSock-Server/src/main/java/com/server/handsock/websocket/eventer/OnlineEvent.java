package com.server.handsock.websocket.eventer;

import com.server.handsock.console.MD5Encoder;
import org.springframework.stereotype.Service;
import com.server.handsock.console.MessageUtils;
import com.server.handsock.console.ConsolePrints;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.SocketIOClient;

import java.util.*;

@Service
public class OnlineEvent {

    private final ConsolePrints consolePrints = new ConsolePrints();
    private final List<Long> uidList = Collections.synchronizedList(new ArrayList<>());
    public final List<String> clientList = Collections.synchronizedList(new ArrayList<>());
    public final List<Map<String, Object>> userList = Collections.synchronizedList(new ArrayList<>());

    private String getClientUUID(SocketIOClient client) {
        String sessionId = client.getSessionId().toString();
        return new MD5Encoder().toMD5(sessionId);
    }

    private Map<String, Object> getUserFromList(Long uid) {
        for (Map<String, Object> user : userList) if (uid.equals(user.get("uid"))) return user;
        return null;
    }

    public void sendUserConnect(SocketIOServer server, SocketIOClient client, String address) {
        String uuid = getClientUUID(client);
        if (!clientList.contains(uuid)) {
            synchronized (clientList) {
                clientList.add(uuid);
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

            @SuppressWarnings("unchecked")
            Map<String, Object> clientObject = (Map<String, Object>) client.getHandshakeData().getAuthToken();
            if (clientObject.get("uid") != null) {
                Long uid = Long.parseLong(clientObject.get("uid").toString());
                for (Long uidS: uidList) {
                    if (uid.equals(uidS)) {
                        if (uidList.stream().filter(item -> item.equals(uidS)).count() == 1) userList.removeIf(obj -> obj.get("uid").equals(uid));
                        uidList.remove(uidS);
                        break;
                    }
                }
            }
            sendMessage(server);
            logConnectionEvent(address, uuid, "New visitor leave");
        }
    }

    public void sendUserOnlineLogin(SocketIOServer server, SocketIOClient client, Map<String, Object> data) {
        Long uid;
        int status;
        try {
            uid = Long.parseLong(data.get("uid").toString());
            status = Integer.parseInt(data.get("status").toString());
        } catch (NumberFormatException e) {
            consolePrints.printErrorLog(e);
            return;
        }
        if (status == 0) {
            uidList.remove(uid);
            userList.removeIf(obj -> obj.get("uid").equals(uid));
        } else {
            uidList.add(uid);
            Map<String, Object> user = getUserFromList(uid);
            if (user == null) {
                user = new HashMap<>();
                user.put("uid", uid);
                user.put("uuid", getClientUUID(client));
                user.put("login", true);
                user.put("platform", data.get("platform"));
                userList.add(user);
            }
        }
        sendMessage(server);
    }

    private void sendMessage(SocketIOServer server) {
        new MessageUtils().sendGlobalMessage(server, "[ONLINE]", Map.of("data", userList));
    }

    private void logConnectionEvent(String address, String uuid, String event) {
        consolePrints.printInfoLogV1(event + " " + address + " " + uuid);
    }
}

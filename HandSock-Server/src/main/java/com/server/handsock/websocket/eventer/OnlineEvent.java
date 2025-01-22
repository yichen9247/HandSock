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
            if (clientObject.get("uid") != null && !clientObject.get("uid").toString().trim().isEmpty()) {
                Long uid = Long.parseLong(clientObject.get("uid").toString());
                for (Iterator<Map<String, Object>> it = userList.iterator(); it.hasNext(); ) {
                    Map<String, Object> user = it.next();
                    if (uid.equals(user.get("uid"))) {
                        @SuppressWarnings("unchecked")
                        List<String> userUuids = (List<String>) user.get("uuids");
                        if (userUuids == null) {
                            userUuids = new ArrayList<>();
                            user.put("uuids", userUuids);
                        }
                        userUuids.remove(uuid);
                        if (userUuids.isEmpty()) it.remove();
                        break;
                    }
                }
            }
            sendMessage(server);
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

        Map<String, Object> user = getUserFromList(uid);
        if (status == 0) {
            if (user != null) {
                @SuppressWarnings("unchecked")
                List<String> userUuids = (List<String>) user.get("uuids");
                if (userUuids != null) {
                    String uuid = getClientUUID(client);
                    userUuids.remove(uuid);
                    if (userUuids.isEmpty()) userList.remove(user);
                }
            }
        } else {
            if (user == null) {
                user = new HashMap<>();
                user.put("uid", uid);
                List<String> userUuids = new ArrayList<>();
                userUuids.add(getClientUUID(client));
                user.put("uuids", userUuids);
                user.put("login", true);
                user.put("platform", data.get("platform"));
                userList.add(user);
            } else {
                @SuppressWarnings("unchecked")
                List<String> userUuids = (List<String>) user.get("uuids");
                if (userUuids == null) {
                    userUuids = new ArrayList<>();
                    user.put("uuids", userUuids);
                }
                userUuids.add(getClientUUID(client));
            }
        }
        sendMessage(server);
    }

    private void sendMessage(SocketIOServer server) {
        new MessageUtils().sendGlobalMessage(server, "[ONLINE]", Map.of("data", userList));
    }
}
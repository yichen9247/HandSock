package com.server.handsock.clients.man;

import com.server.handsock.clients.mod.ClientChatModel;
import com.server.handsock.utils.ConsoleUtils;
import com.server.handsock.utils.HandUtils;
import com.server.handsock.utils.IDGenerator;

import java.util.HashMap;
import java.util.Map;

public class ClientChatManage {
    private final HandUtils handUtils;
    private final IDGenerator idGenerator;
    private final ConsoleUtils consoleUtils;

    public ClientChatManage(HandUtils handUtils, ConsoleUtils consoleUtils, IDGenerator idGenerator) {
        this.handUtils = handUtils;
        this.idGenerator = idGenerator;
        this.consoleUtils = consoleUtils;
    }

    public Map<String, Object> insertChatMessage(ClientChatModel clientChatModel, String type, long uid, long gid, String address, String content) {
        clientChatModel.setUid(uid);
        clientChatModel.setGid(gid);
        clientChatModel.setType(type);
        clientChatModel.setDeleted(0);
        clientChatModel.setAddress(address);
        clientChatModel.setTime(handUtils.formatTimeForString("yyyy-MM-dd HH:mm:ss"));
        clientChatModel.setContent(content);
        clientChatModel.setSid(idGenerator.generateRandomMessageId(uid, gid, address));
        consoleUtils.printInfoLog("User Message " + clientChatModel.getAddress() + " " + clientChatModel.getUid() + " " + clientChatModel.getGid() + " " + clientChatModel.getSid());

        return new HashMap<>() {{
            put("sid", clientChatModel.getSid());
            put("uid", clientChatModel.getUid());
            put("gid", clientChatModel.getGid());
            put("type", clientChatModel.getType());
            put("time", clientChatModel.getTime());
            put("deleted", clientChatModel.getDeleted());
            put("content", clientChatModel.getContent());
        }};
    }
}

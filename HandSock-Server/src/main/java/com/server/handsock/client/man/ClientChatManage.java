package com.server.handsock.client.man;

import com.server.handsock.common.model.MessageModel;
import com.server.handsock.common.utils.ConsoleUtils;
import com.server.handsock.common.utils.HandUtils;
import com.server.handsock.common.utils.IDGenerator;

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

    public Map<String, Object> insertChatMessage(MessageModel messageModel, String type, long uid, long gid, String address, String content) {
        messageModel.setUid(uid);
        messageModel.setGid(gid);
        messageModel.setType(type);
        messageModel.setDeleted(0);
        messageModel.setAddress(address);
        messageModel.setTime(handUtils.formatTimeForString("yyyy-MM-dd HH:mm:ss"));
        messageModel.setContent(content);
        messageModel.setSid(idGenerator.generateRandomMessageId(uid, gid, address));
        consoleUtils.printInfoLog("User Message " + messageModel.getAddress() + " " + messageModel.getUid() + " " + messageModel.getGid() + " " + messageModel.getSid());

        return new HashMap<>() {{
            put("sid", messageModel.getSid());
            put("uid", messageModel.getUid());
            put("gid", messageModel.getGid());
            put("type", messageModel.getType());
            put("time", messageModel.getTime());
            put("deleted", messageModel.getDeleted());
            put("content", messageModel.getContent());
        }};
    }
}

package com.server.handsock.clients.man;

import com.server.handsock.console.UtilityService;
import com.server.handsock.clients.mod.ClientChatModel;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.IDGenerator;

import java.util.HashMap;
import java.util.Map;

public class ClientChatManage {
    public Map<String, Object> insertChatMessage(ClientChatModel clientChatModel, String type, long uid, long gid, String address, String content) {
        clientChatModel.setUid(uid);
        clientChatModel.setGid(gid);
        clientChatModel.setType(type);
        clientChatModel.setDeleted(0);
        clientChatModel.setAddress(address);
        clientChatModel.setTime(new UtilityService().formatTime("yyyy-MM-dd HH:mm:ss"));
        clientChatModel.setContent(content);
        clientChatModel.setSid(new IDGenerator().generateRandomMessageId(uid, gid, address));
        new ConsolePrints().printInfoLogV1("User Message " + clientChatModel.getAddress() + " " + clientChatModel.getUid() + " " + clientChatModel.getGid() + " " + clientChatModel.getSid());

        return new HashMap<>(){{
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

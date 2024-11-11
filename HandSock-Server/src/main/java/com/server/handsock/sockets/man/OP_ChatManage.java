package com.server.handsock.sockets.man;

import com.server.handsock.sockets.mod.OP_ChatModel;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.TimeFormatUtils;
import com.server.handsock.generat.IDGenerator;

import java.util.HashMap;
import java.util.Map;

public class OP_ChatManage {
    public Map<String, Object> insertChatMessage(OP_ChatModel OPChatModel, String type, long uid, long gid, String address, String content) {
        OPChatModel.setUid(uid);
        OPChatModel.setGid(gid);
        OPChatModel.setType(type);
        OPChatModel.setDeleted(0);
        OPChatModel.setAddress(address);
        OPChatModel.setTime(new TimeFormatUtils().formatTime("yyyy-MM-dd HH:mm:ss"));
        OPChatModel.setContent(content);
        OPChatModel.setSid(new IDGenerator().generateRandomMessageId(uid, gid, address));
        new ConsolePrints().printInfoLogV1("User Message " + OPChatModel.getAddress() + " " + OPChatModel.getUid() + " " + OPChatModel.getGid() + " " + OPChatModel.getSid());

        return new HashMap<>(){{
            put("sid", OPChatModel.getSid());
            put("uid", OPChatModel.getUid());
            put("gid", OPChatModel.getGid());
            put("type", OPChatModel.getType());
            put("time", OPChatModel.getTime());
            put("deleted", OPChatModel.getDeleted());
            put("content", OPChatModel.getContent());
        }};
    }
}

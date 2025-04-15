package com.server.handsock.client.man;

import com.server.handsock.admin.mod.ServerUserModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ClientUserManage {
    public Map<String, Object> updateNick(ServerUserModel serverUserModel, long uid, String nick) {
        serverUserModel.setUid(uid);
        serverUserModel.setNick(nick);
        return new HashMap<>() {{
            put("uid", serverUserModel.getUid());
            put("nick", serverUserModel.getNick());
        }};
    }

    public Map<String, Object> updateQQId(ServerUserModel serverUserModel, long uid, String qqId) {
        serverUserModel.setUid(uid);
        serverUserModel.setQqId(qqId);
        return new HashMap<>() {{
            put("qq", serverUserModel.getQqId() != null);
        }};
    }

    public Map<String, Object> updateAvatar(ServerUserModel serverUserModel, long uid, String path) {
        serverUserModel.setUid(uid);
        serverUserModel.setAvatar(path);
        return new HashMap<>() {{
            put("uid", serverUserModel.getUid());
            put("avatar", serverUserModel.getAvatar());
        }};
    }

    public Map<String, Object> updateUserName(ServerUserModel serverUserModel, long uid, String username) {
        serverUserModel.setUid(uid);
        serverUserModel.setUsername(username);
        return new HashMap<>() {{
            put("uid", serverUserModel.getUid());
            put("username", serverUserModel.getUsername());
        }};
    }

    public void updatePassword(ServerUserModel serverUserModel, long uid, String password) {
        serverUserModel.setUid(uid);
        serverUserModel.setPassword(password);
    }

    public Map<String, Object> registerUser(ServerUserModel serverUserModel, long uid, String username, String password, String address) {
        serverUserModel.setUid(uid);
        serverUserModel.setNick("热心网友");
        serverUserModel.setAvatar("0/default/" + new Random().nextInt(7) + ".png");
        serverUserModel.setUsername(username);
        serverUserModel.setPassword(password);

        return new HashMap<>() {{
            put("userinfo", new HashMap<>() {{
                put("uid", uid);
                put("isAdmin", 0);
                put("isRobot", 0);
                put("nick", serverUserModel.getNick());
                put("avatar", serverUserModel.getAvatar());
                put("username", serverUserModel.getUsername());
            }});
        }};
    }
}

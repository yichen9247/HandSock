package com.server.handsock.clients.man;

import com.server.handsock.console.ConsolePrints;
import com.server.handsock.admin.mod.ServerUserModel;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class ClientUserManage {
    public Map<String, Object> updateNick(ServerUserModel server_userModel, long uid, String nick) {
        server_userModel.setUid(uid);
        server_userModel.setNick(nick);
        return new HashMap<>(){{
            put("uid", server_userModel.getUid());
           put("nick", server_userModel.getNick());
        }};
    }

    public Map<String, Object> updateAvatar(ServerUserModel server_userModel, long uid, String path) {
        server_userModel.setUid(uid);
        server_userModel.setAvatar(path);
        return new HashMap<>(){{
            put("uid", server_userModel.getUid());
            put("avatar", server_userModel.getAvatar());
        }};
    }

    public Map<String, Object> updateUserName(ServerUserModel server_userModel, long uid, String username) {
        server_userModel.setUid(uid);
        server_userModel.setUsername(username);
        return new HashMap<>(){{
            put("uid", server_userModel.getUid());
            put("username", server_userModel.getUsername());
        }};
    }

    public void updatePassword(ServerUserModel server_userModel, long uid, String password) {
        server_userModel.setUid(uid);
        server_userModel.setPassword(password);
    }

    public Map<String, Object> registerUser(ServerUserModel server_userModel, long uid, String username, String password, String address) {
        server_userModel.setUid(uid);
        server_userModel.setNick("热心网友");
        server_userModel.setAvatar("0/default/" + new Random().nextInt(7) + ".png");
        server_userModel.setUsername(username);
        server_userModel.setPassword(password);
        new ConsolePrints().printInfoLogV2("User Register " + address + " " + server_userModel.getUid());

        return new HashMap<>(){{
            put("userinfo", new HashMap<>(){{
                put("uid", uid);
                put("isAdmin", 0);
                put("isRobot", 0);
                put("nick", server_userModel.getNick());
                put("avatar", server_userModel.getAvatar());
                put("username", server_userModel.getUsername());
            }});
        }};
    }
}

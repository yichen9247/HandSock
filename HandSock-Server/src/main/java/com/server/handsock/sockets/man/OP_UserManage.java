package com.server.handsock.sockets.man;

import com.server.handsock.console.ConsolePrints;
import com.server.handsock.admin.mod.OR_UserModel;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class OP_UserManage {
    public Map<String, Object> updateNick(OR_UserModel or_userModel, long uid, String nick) {
        or_userModel.setUid(uid);
        or_userModel.setNick(nick);
        return new HashMap<>(){{
            put("uid", or_userModel.getUid());
           put("nick", or_userModel.getNick());
        }};
    }

    public Map<String, Object> updateAvatar(OR_UserModel or_userModel, long uid, String path) {
        or_userModel.setUid(uid);
        or_userModel.setAvatar(path);
        return new HashMap<>(){{
            put("uid", or_userModel.getUid());
            put("avatar", or_userModel.getAvatar());
        }};
    }

    public Map<String, Object> updateUserName(OR_UserModel or_userModel, long uid, String username) {
        or_userModel.setUid(uid);
        or_userModel.setUsername(username);
        return new HashMap<>(){{
            put("uid", or_userModel.getUid());
            put("username", or_userModel.getUsername());
        }};
    }

    public void updatePassword(OR_UserModel or_userModel, long uid, String password) {
        or_userModel.setUid(uid);
        or_userModel.setPassword(password);
    }

    public Map<String, Object> registerUser(OR_UserModel or_userModel, long uid, String username, String password, String address) {
        or_userModel.setUid(uid);
        or_userModel.setNick("热心网友");
        or_userModel.setAvatar("0/default/" + new Random().nextInt(7) + ".png");
        or_userModel.setUsername(username);
        or_userModel.setPassword(password);
        new ConsolePrints().printInfoLogV2("User Register " + address + " " + or_userModel.getUid());

        return new HashMap<>(){{
            put("userinfo", new HashMap<>(){{
                put("uid", uid);
                put("isAdmin", 0);
                put("isRobot", 0);
                put("nick", or_userModel.getNick());
                put("avatar", or_userModel.getAvatar());
                put("username", or_userModel.getUsername());
            }});
        }};
    }
}

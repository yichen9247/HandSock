package com.server.handsock.admin.man;

import com.server.handsock.console.MD5Encoder;
import com.server.handsock.admin.mod.OR_UserModel;

public class OR_UserManage {

    public void updateUserPassword(OR_UserModel or_userModel, Long uid, String password) {
        or_userModel.setUid(uid);
        or_userModel.setPassword(new MD5Encoder().toMD5(password));
    }

    public void updateUserInfo(OR_UserModel or_userModel, Long uid, String username, String nick, String avatar, Integer robot) {
        or_userModel.setUid(uid);
        or_userModel.setNick(nick);
        or_userModel.setAvatar(avatar);
        or_userModel.setIsRobot(robot);
        or_userModel.setUsername(username);
    }

    public void updateUserTabooStatus(OR_UserModel or_userModel, Long uid, String status) {
        or_userModel.setUid(uid);
        or_userModel.setTaboo(status);
    }
}

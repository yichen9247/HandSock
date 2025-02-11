package com.server.handsock.admin.man;

import com.server.handsock.admin.mod.ServerUserModel;
import com.server.handsock.console.UtilityService;

public class ServerUserManage {

    public void updateUserPassword(ServerUserModel server_userModel, Long uid, String password) {
        server_userModel.setUid(uid);
        server_userModel.setPassword(new UtilityService().encodeStringToMD5(password));
    }

    public void updateUserInfo(ServerUserModel server_userModel, Long uid, String username, String nick, String avatar, Integer robot) {
        server_userModel.setUid(uid);
        server_userModel.setNick(nick);
        server_userModel.setAvatar(avatar);
        server_userModel.setIsRobot(robot);
        server_userModel.setUsername(username);
    }

    public void updateUserTabooStatus(ServerUserModel server_userModel, Long uid, String status) {
        server_userModel.setUid(uid);
        server_userModel.setTaboo(status);
    }
}

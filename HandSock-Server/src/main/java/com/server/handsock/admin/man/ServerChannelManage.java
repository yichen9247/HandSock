package com.server.handsock.admin.man;

import com.server.handsock.admin.mod.ServerChannelModel;

public class ServerChannelManage {

    public void setChan(ServerChannelModel serverChannelModel, Long gid, String name, String avatar, String notice, Boolean aiRole) {
        serverChannelModel.setGid(gid);
        serverChannelModel.setName(name);
        serverChannelModel.setAvatar(avatar);
        serverChannelModel.setNotice(notice);
        serverChannelModel.setAiRole(aiRole ? 1 : 0);
    }

    public void updateChanOpenStatus(ServerChannelModel serverChannelModel, Long gid, Integer status) {
        serverChannelModel.setGid(gid);
        serverChannelModel.setOpen(status);
    }

    public void updateChanActiveStatus(ServerChannelModel serverChannelModel, Long gid, Integer status) {
        serverChannelModel.setGid(gid);
        serverChannelModel.setActive(status);
    }
}

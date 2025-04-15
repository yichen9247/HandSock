package com.server.handsock.admin.man;

import com.server.handsock.common.model.ChannelModel;

public class ServerChannelManage {

    public void setChan(ChannelModel channelModel, Long gid, String name, String avatar, String notice, Boolean aiRole) {
        channelModel.setGid(gid);
        channelModel.setName(name);
        channelModel.setAvatar(avatar);
        channelModel.setNotice(notice);
        channelModel.setAiRole(aiRole ? 1 : 0);
    }

    public void updateChanOpenStatus(ChannelModel channelModel, Long gid, Integer status) {
        channelModel.setGid(gid);
        channelModel.setOpen(status);
    }

    public void updateChanActiveStatus(ChannelModel channelModel, Long gid, Integer status) {
        channelModel.setGid(gid);
        channelModel.setActive(status);
    }
}

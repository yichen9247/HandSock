package com.server.handsock.admin.man;

import com.server.handsock.admin.mod.OR_ChanModel;

public class OR_ChanManage {

    public void setChan(OR_ChanModel or_chanModel, Long gid, String name, String avatar, String notice) {
        or_chanModel.setGid(gid);
        or_chanModel.setName(name);
        or_chanModel.setAvatar(avatar);
        or_chanModel.setNotice(notice);
    }

    public void updateChanOpenStatus(OR_ChanModel or_chanModel, Long gid, Integer status) {
        or_chanModel.setGid(gid);
        or_chanModel.setOpen(status);
    }

    public void updateChanActiveStatus(OR_ChanModel or_chanModel, Long gid, Integer status) {
        or_chanModel.setGid(gid);
        or_chanModel.setActive(status);
    }
}

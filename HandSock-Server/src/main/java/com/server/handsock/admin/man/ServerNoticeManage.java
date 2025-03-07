package com.server.handsock.admin.man;

import com.server.handsock.admin.mod.ServerNoticeModel;

public class ServerNoticeManage {
    public void setNotice(ServerNoticeModel serverNoticeModel, String title, String content) {
        serverNoticeModel.setTitle(title);
        serverNoticeModel.setContent(content);
    }

    public void updateNotice(ServerNoticeModel serverNoticeModel, Integer nid, String title, String content) {
        serverNoticeModel.setNid(nid);
        setNotice(serverNoticeModel, title, content);
    }
}

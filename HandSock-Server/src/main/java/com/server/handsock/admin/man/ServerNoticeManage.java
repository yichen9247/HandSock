package com.server.handsock.admin.man;

import com.server.handsock.common.model.NoticeModel;

public class ServerNoticeManage {
    public void setNotice(NoticeModel noticeModel, String title, String content) {
        noticeModel.setTitle(title);
        noticeModel.setContent(content);
    }

    public void updateNotice(NoticeModel noticeModel, Integer nid, String title, String content) {
        noticeModel.setNid(nid);
        setNotice(noticeModel, title, content);
    }
}

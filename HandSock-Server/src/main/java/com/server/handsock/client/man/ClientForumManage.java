package com.server.handsock.client.man;

import com.server.handsock.common.model.ForumModel;

public class ClientForumManage {
    public void insertForumPost(ForumModel forumModel, Long uid, String title, String content, String image) {
        forumModel.setUid(uid);
        forumModel.setImage(image);
        forumModel.setTitle(title);
        forumModel.setContent(content);
    }
}

package com.server.handsock.client.man;

import com.server.handsock.common.model.CommentModel;

public class ClientCommentManage {
    public void insertPostComment(CommentModel commentModel, Integer pid, Integer parent, Long uid, String content) {
        commentModel.setPid(pid);
        commentModel.setUid(uid);
        commentModel.setContent(content);
        if (parent != null) commentModel.setParent(parent);
    }
}

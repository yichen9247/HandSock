package com.server.handsock.socket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.common.data.SocketAddPostComment;
import com.server.handsock.common.utils.GlobalService;
import com.server.handsock.socket.handler.CommentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentListener {
    private final SocketIOServer server;
    private final CommentHandler commentHandler;

    @Autowired
    public CommentListener(CommentHandler commentHandler) {
        this.commentHandler = commentHandler;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener() {
        server.addEventListener("[ADD:FORUM:COMMENT]", SocketAddPostComment.class, commentHandler::handleAddPostComment);
    }
}

package com.server.handsock.socket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.common.data.SocketAddForumPost;
import com.server.handsock.common.utils.GlobalService;
import com.server.handsock.socket.handler.ForumHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForumListener {
    private final SocketIOServer server;
    private final ForumHandler forumHandler;

    @Autowired
    public ForumListener(ForumHandler forumHandler) {
        this.forumHandler = forumHandler;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener() {
        server.addEventListener("[ADD:FORUM:POST]", SocketAddForumPost.class, forumHandler::handleAddForumPost);
    }
}

package com.server.handsock.websocket.eventer;

import org.springframework.stereotype.Service;
import com.server.handsock.console.MessageUtils;
import com.server.handsock.console.ConsolePrints;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.admin.service.OR_ChatService;
import com.server.handsock.sockets.service.OP_UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.ArrayList;

@Service
public class RobotsEvent {

    private final OP_UserService OPUserService;
    private final OR_ChatService or_chatService;
    private final ConsolePrints consolePrints = new ConsolePrints();

    @Autowired
    public RobotsEvent(OP_UserService OPUserService, OR_ChatService or_chatService) {
        this.OPUserService = OPUserService;
        this.or_chatService = or_chatService;
    }

    public ArrayList<String> commandList = new ArrayList<>(){{
        add("handsock clear");
        add("handsock help");
    }};

    public String handleRobotCommand(SocketIOClient client, String command, SocketIOServer server) {
        try {
            if (Objects.equals(command, commandList.get(0)) && OPUserService.getUserAdminStatus(client)) {
                or_chatService.clearAllChatHistory();
                new ConsolePrints().printInfoLogV2("Chat history cleared!");
                new MessageUtils().sendGlobalMessage(server, "[RE:HISTORY:CLEAR]", null);
                Thread.sleep(1000);
            }

            if (Objects.equals(command, commandList.get(1))) {
                return "HandSock 是一款有趣的聊天应用，基于 Typescript，Mybatis-Plus，Springboot, Vue3 和 Socket.io，Redis 等技术开发";
            };
            return null;
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return null;
        }
    }
}

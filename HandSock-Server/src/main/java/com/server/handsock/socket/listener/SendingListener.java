package com.server.handsock.socket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.common.data.SocketUserMessage;
import com.server.handsock.common.props.AiProp;
import com.server.handsock.common.utils.GlobalService;
import com.server.handsock.service.ClientService;
import com.server.handsock.socket.eventer.RobotSender;
import com.server.handsock.socket.handler.AIChatHandler;
import com.server.handsock.socket.handler.SendingHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service @Getter @Setter
public class SendingListener {

    private final SocketIOServer server;
    private final RobotSender robotSender;
    private final AiProp aiProp;
    private final AIChatHandler aiChatHandler;
    private final ClientService clientService;
    private final SendingHandler sendingHandler;

    @Autowired
    public SendingListener(AiProp aiProp, SendingHandler sendingHandler, ClientService clientService, RobotSender robotSender, AIChatHandler aiChatHandler) {
        this.aiProp = aiProp;
        this.robotSender = robotSender;
        this.clientService = clientService;
        this.aiChatHandler = aiChatHandler;
        this.sendingHandler = sendingHandler;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener() {
        server.addEventListener("[SEND:MESSAGE]", SocketUserMessage.class, (client, data, ackSender) -> {
            sendingHandler.handleSendMessage(server, client, data, ackSender);
        });

        server.addEventListener("[SEND:AI:CHAT:MESSAGE]", SocketUserMessage.class, (client, data, ackSender) -> {
            aiChatHandler.handleAIChatMessage(client, data, ackSender, aiProp);
        });
    }
}
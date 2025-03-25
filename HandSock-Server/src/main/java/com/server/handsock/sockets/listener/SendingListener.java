package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.props.AiProp;
import com.server.handsock.services.ClientService;
import com.server.handsock.sockets.eventer.RobotSender;
import com.server.handsock.sockets.handler.AIChatHandler;
import com.server.handsock.sockets.handler.SendingHandler;
import com.server.handsock.utils.GlobalService;
import com.server.handsock.utils.HandUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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
        this.robotSender = robotSender;
        this.aiProp = aiProp;
        this.clientService = clientService;
        this.aiChatHandler = aiChatHandler;
        this.sendingHandler = sendingHandler;
        this.server = GlobalService.INSTANCE.getSocketIOServer();
    }

    public void addEventListener(HandUtils handUtils) {
        server.addEventListener("[SEND:MESSAGE]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            sendingHandler.handleSendMessage(server, client, typedData, ackSender);
        });

        server.addEventInterceptor((client, eventName, args, ackRequest) -> {
            try {
                Object dataEvent = !args.isEmpty() ? args.get(0) : null;
                if (eventName.equals("[SEND:MESSAGE]") && dataEvent != null)
                    robotSender.handleSendMessageOnBot(dataEvent, client, ackRequest);
            } catch (Exception e) {
                ackRequest.sendAckData(handUtils.printErrorLog(e));
            }
        });

        server.addEventListener("[SEND:AI:CHAT:MESSAGE]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            aiChatHandler.handleAIChatMessage(client, typedData, ackSender, aiProp);
        });
    }
}
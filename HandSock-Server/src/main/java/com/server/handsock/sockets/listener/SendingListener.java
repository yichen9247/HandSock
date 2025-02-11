package com.server.handsock.sockets.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.console.AIProperties;
import com.server.handsock.services.ClientService;
import com.server.handsock.sockets.eventer.RobotSender;
import com.server.handsock.sockets.handler.AIChatHandler;
import com.server.handsock.sockets.handler.SendingHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service @Getter @Setter
public class SendingListener {

    private final AIProperties aiProperties;

    @Autowired
    public SendingListener(AIProperties aiProperties) {
        this.aiProperties = aiProperties;
    }

    public void addEventListener(SocketIOServer server, Map<String, Object> service, Map<String, Object> clientServiceList, Map<String, Object> serverServiceList) {
        AIChatHandler aiChatHandler = new AIChatHandler(service, clientServiceList, serverServiceList);
        SendingHandler sendingHandler = new SendingHandler(service, clientServiceList, serverServiceList);

        server.addEventListener("[SEND:MESSAGE]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            sendingHandler.handleSendMessage(server, client, typedData, ackSender);
        });

        ClientService clientService = new ClientService(service, clientServiceList);
        server.addEventInterceptor((client, eventName, args, ackRequest) -> {
            try {
                Object dataEvent = args.get(0);
                if (eventName.equals("[SEND:MESSAGE]") && dataEvent != null) new RobotSender(service, clientServiceList, serverServiceList).handleSendMessageOnBot(server, client, dataEvent, ackRequest);
            } catch (Exception e) {
                clientService.handleException(e, ackRequest);
            }
        });

        server.addEventListener("[SEND:AI:CHAT:MESSAGE]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            aiChatHandler.handleAIChatMessage(client, typedData, ackSender, aiProperties);
        });
    }
}

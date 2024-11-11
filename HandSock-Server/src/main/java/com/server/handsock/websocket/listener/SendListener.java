package com.server.handsock.websocket.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.service.ClientService;
import com.server.handsock.websocket.eventer.RobotSender;
import com.server.handsock.websocket.handler.SendHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SendListener {
    public void addEventListener(SocketIOServer server, Map<String, Object> service) {
        SendHandler sendHandler = new SendHandler(service);

        server.addEventListener("[SEND:MESSAGE]", Map.class, (client, data, ackSender) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedData = (Map<String, Object>) data;
            sendHandler.handleSendMessage(server, client, typedData, ackSender);
        });

        ClientService clientService = new ClientService(service);
        server.addEventInterceptor((client, eventName, args, ackRequest) -> {
            try {
                Object dataEvent = args.get(0);
                if (eventName.equals("[SEND:MESSAGE]") && dataEvent != null) new RobotSender(service).handleSendMessageOnBot(server, client, dataEvent, ackRequest);
            } catch (Exception e) {
                clientService.handleException(e, ackRequest);
            }
        });
    }
}

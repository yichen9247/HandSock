package com.server.handsock.sockets.listener;

import com.server.handsock.props.HandProp;
import com.server.handsock.utils.GlobalService;
import com.server.handsock.utils.HandUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class CheckListener {

    private final HandProp handProp;

    @Autowired
    public CheckListener(HandProp handProp) {
        this.handProp = handProp;
    }

    public void addEventListener(HandUtils handUtils) {
        Objects.requireNonNull(GlobalService.INSTANCE.getSocketIOServer()).addEventListener("[CLIENT:CHECK]", Map.class, (client, data, ackSender) -> {
            if (data.get("version").equals(handProp.getAppVersion())) {
                ackSender.sendAckData(handUtils.handleResultByCode(200, null, "服务正常"));
            } else ackSender.sendAckData(handUtils.handleResultByCode(500, null, "服务异常"));
        });
    }
}
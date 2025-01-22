package com.server.handsock.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service @Getter @Setter
public class SocketService {
    @Value("${handsock.port}")
    public int port;

    @Value("${handsock.host}")
    public String host;

    @Value("${handsock.origin}")
    public String origin;

    @Value("${handsock.pingTimeout}")
    public int pingTimeout;

    @Value("${handsock.pingInterval}")
    public int pingInterval;

    @Value("${handsock.upgradeTimeout}")
    public int upgradeTimeout;

    public String appVersion = "2.0.1-B250101";
}

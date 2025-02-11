package com.server.handsock.console;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter @Getter @Component
@ConfigurationProperties(prefix = "handsock")
public class AppProperties {
    private int port;
    private String host;
    private String origin;
    private int pingTimeout;
    private int pingInterval;
    private int upgradeTimeout;
    private String appVersion = "2.1.2-B250211";
}

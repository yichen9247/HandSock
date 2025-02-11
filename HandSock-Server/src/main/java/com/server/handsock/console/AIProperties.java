package com.server.handsock.console;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "ai")
public class AIProperties {
    private String url;
    private String path;
    private String model;
    private String token;
}

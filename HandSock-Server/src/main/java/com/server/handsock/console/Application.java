package com.server.handsock.console;

import java.lang.management.ManagementFactory;

public class Application {
    public String getSystemUptime() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        long seconds = uptime / 1000;
        long days = seconds / 86400;
        seconds %= 86400;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%d天%d小时%d分钟%d秒", days, hours, minutes, seconds);
    }
}

package com.server.handsock.console;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatUtils {
    public String formatTime(String pattern) {
        OffsetDateTime now = OffsetDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return now.format(formatter);
    }
}

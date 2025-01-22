package com.server.handsock.admin.service;

import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class OR_LogService {

    private static final String logFilePath = "server.log";
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    public Map<String, Object> getSystemLogs() {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> lines = Files.lines(Paths.get(logFilePath))) {
            lines.forEach(line -> contentBuilder.append(line).append(System.lineSeparator()));
            return handleResults.handleResultByCode(200, contentBuilder.toString(), "获取成功");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> deleteSystemLogs() {
        try {
            Files.write(Paths.get(logFilePath), new byte[0]);
            return handleResults.handleResultByCode(200, null, "日志清空成功");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }
}

package com.server.handsock.console;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class ConsolePrints {
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String logFilePath = "server.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public void printErrorLog(Object content) {
        printLog(ANSI_RED, content);
    }

    public void printSuccessLog(Object content) {
        printLog(ANSI_GREEN, content);
    }

    public void printInfoLogV1(Object content) {
        printLog(ANSI_BLUE, content);
    }

    public void printInfoLogV2(Object content) {
        printLog(ANSI_CYAN, content);
    }

    private void writeInLog(String content) {
        File file = new File(logFilePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (!file.exists()) {
                boolean isCreated = file.createNewFile();
                if (isCreated) {
                    writer.write(content + "\n");
                } else printStaticLog("Failed to create new log file!");
            } else writer.write(content + "\n");
        } catch (IOException e) {
            printStaticLog(e);
        }
    }

    private void printStaticLog(Object content) {
        OffsetDateTime now = OffsetDateTime.now();
        String formattedDateTime = now.format(FORMATTER);
        String staticContent = "[" + "HandSock" + " " + formattedDateTime + "]" + content;
        System.out.println(ConsolePrints.ANSI_RED + staticContent + ANSI_RESET);
    }

    private void printLog(String ansiColor, Object content) {
        OffsetDateTime now = OffsetDateTime.now();
        String formattedDateTime = now.format(FORMATTER);
        String staticContent = "[" + "HandSock" + " " + formattedDateTime + "]" + content;
        writeInLog(staticContent);
        System.out.println(ansiColor + staticContent + ANSI_RESET);
    }
}

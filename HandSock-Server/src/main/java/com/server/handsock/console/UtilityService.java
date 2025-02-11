package com.server.handsock.console;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.lang.management.ManagementFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class UtilityService {
    private final ConsolePrints consolePrints = new ConsolePrints();

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

    public String formatTime(String pattern) {
        OffsetDateTime now = OffsetDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return now.format(formatter);
    }

    public Boolean checkImageValidExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        boolean isValidExtension = fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".webp") || fileName.endsWith(".gif");
        if (!isValidExtension) return false;
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) return false;
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return false;
        }
        return true;
    }

    public String encodeStringToMD5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(text.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            consolePrints.printErrorLog(e);
            return "error";
        }
    }

    public void sendGlobalMessage(SocketIOServer server, String event, Object content) {
        BroadcastOperations broadcastOperations = server.getBroadcastOperations();
        broadcastOperations.sendEvent(event, content);
    }
}

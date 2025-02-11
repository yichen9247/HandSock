package com.server.handsock.console;

import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

@Service
public class IDGenerator {
    private final UtilityService utilityService = new UtilityService();

    public long generateRandomId(int length) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String yearPrefix = String.valueOf(currentYear).substring(currentYear - 2000 > 0 ? 2 : 0);
        Random random = new Random();
        String randomSuffix = String.format("%0" + length + "d", random.nextInt(1000000));
        return Long.parseLong(yearPrefix + randomSuffix);
    }

    public String generateRandomMessageId(Long uid, long gid, String address) {
        return utilityService.encodeStringToMD5(UUID.randomUUID() + "-" + utilityService.encodeStringToMD5(uid + String.valueOf(gid) + address));
    }

    public String generateRandomReportedId(String sid, Long reporter_id, Long reported_id) {
        return utilityService.encodeStringToMD5(UUID.randomUUID() + "-" + utilityService.encodeStringToMD5(sid + reporter_id + reported_id));
    }

    public String generateRandomFileId(Long uid, String name, String path, String time) {
        return utilityService.encodeStringToMD5(UUID.randomUUID() + "-" + utilityService.encodeStringToMD5(uid + name + path + time));
    }
}

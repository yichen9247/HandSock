package com.server.handsock.generat;

import com.server.handsock.console.MD5Encoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

@Service
public class IDGenerator {
    public long generateRandomId(int length) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String yearPrefix = String.valueOf(currentYear).substring(currentYear - 2000 > 0 ? 2 : 0);
        Random random = new Random();
        String randomSuffix = String.format("%0" + length + "d", random.nextInt(1000000));
        return Long.parseLong(yearPrefix + randomSuffix);
    }

    public String generateRandomMessageId(Long uid, long gid, String address) {
        return new MD5Encoder().toMD5(UUID.randomUUID() + "-" + new MD5Encoder().toMD5(uid + String.valueOf(gid) + address));
    }

    public String generateRandomReportedId(String sid, Long reporter_id, Long reported_id) {
        return new MD5Encoder().toMD5(UUID.randomUUID() + "-" + new MD5Encoder().toMD5(sid + reporter_id + reported_id));
    }

    public String generateRandomFileId(Long uid, String name, String path, String time) {
        return new MD5Encoder().toMD5(UUID.randomUUID() + "-" + new MD5Encoder().toMD5(uid + name + path + time));
    }
}

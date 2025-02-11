package com.server.handsock.services;

import com.server.handsock.console.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private final UtilityService utilityService = new UtilityService();
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public TokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void removeUserToken(long uid) {
        String cachedToken = redisTemplate.opsForValue().get(String.valueOf(uid));
        if (cachedToken != null) redisTemplate.delete(String.valueOf(uid));
    }

    public String generateUserToken(long uid, String username, String address) {
        String formatTime = utilityService.formatTime("yyyy-MM-dd HH:mm:ss");
        String enCodeString = UUID.randomUUID().toString() + System.currentTimeMillis();
        String token = utilityService.encodeStringToMD5(uid + username + enCodeString + formatTime + address);
        redisTemplate.opsForValue().set("handsock-userToken:" + uid, token, 30, TimeUnit.DAYS);
        return token;
    }

    public Boolean validUserToken(long uid, String oldToken) {
        String cachedToken = redisTemplate.opsForValue().get("handsock-userToken:" + uid);
        if (cachedToken != null)  {
            return cachedToken.equals(oldToken);
        } else return false;
    }
}

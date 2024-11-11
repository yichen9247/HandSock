package com.server.handsock.admin.service;

import com.server.handsock.console.Application;
import org.springframework.stereotype.Service;
import com.server.handsock.admin.dao.OR_ChanDao;
import com.server.handsock.admin.dao.OR_ChatDao;
import com.server.handsock.admin.dao.OR_UserDao;
import com.server.handsock.console.HandleResults;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.admin.mod.OR_ChatModel;
import com.server.handsock.admin.mod.OR_UserModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

import java.util.Map;
import java.util.HashMap;
import java.util.TimeZone;
import java.time.LocalDate;
import java.time.LocalTime;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.net.UnknownHostException;
import java.time.format.DateTimeFormatter;

@Service
public class OR_DashService {

    private final OR_ChatDao or_chatDao;
    private final OR_ChanDao or_chanDao;
    private final OR_UserDao or_userDao;
    private final OperatingSystem operatingSystem;

    @Autowired
    public OR_DashService(OR_ChatDao or_chatDao, OR_ChanDao or_chanDao, OR_UserDao or_userDao) {
        this.or_chatDao = or_chatDao;
        this.or_chanDao = or_chanDao;
        this.or_userDao = or_userDao;

        SystemInfo si = new SystemInfo();
        this.operatingSystem = si.getOperatingSystem();
    }

    public Map<String, Object> getDashboardData() {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

            Long userTotal = or_userDao.selectCount(null);
            Long chanTotal = or_chanDao.selectCount(null);
            Long todayRegUser = countRecordsInRange(or_userDao, OR_UserModel::getRegTime, startOfDay, endOfDay);
            Long todayChatTotal = countRecordsInRange(or_chatDao, OR_ChatModel::getTime, startOfDay, endOfDay);

            Map<String, Object> result = new HashMap<>();
            result.put("userTotal", userTotal);
            result.put("chanTotal", chanTotal);
            result.put("todayRegUser", todayRegUser);
            result.put("todayChatTotal", todayChatTotal);
            result.put("systemOsInfo", getSystemOsInfo());
            return new HandleResults().handleResultByCode(200, result, "获取成功");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return new HandleResults().handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> getSystemOsInfo() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        return new HashMap<>(){{
            put("osInfo",  System.getProperty("os.name") + " " + operatingSystem.getVersionInfo());
            put("osArch", System.getProperty("os.arch"));
            put("locale", LocaleContextHolder.getLocale());
            put("hostName", localHost.getHostName());
            put("timeZoneId", TimeZone.getDefault().getID());
            put("hostAddress", localHost.getHostAddress());
            put("systemUptime", new Application().getSystemUptime());
        }};
    }

    private <T> Long countRecordsInRange(BaseMapper<T> dao, SFunction<T, String> timeField, LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(timeField, start.format(formatter)).lt(timeField, end.format(formatter));
        return dao.selectCount(queryWrapper);
    }
}

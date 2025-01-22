package com.server.handsock.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.server.handsock.admin.dao.OR_ChanDao;
import com.server.handsock.admin.dao.OR_ChatDao;
import com.server.handsock.admin.dao.OR_UserDao;
import com.server.handsock.admin.mod.OR_ChatModel;
import com.server.handsock.admin.mod.OR_UserModel;
import com.server.handsock.console.Application;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.service.SocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Service
public class OR_DashService {

    private final OR_ChatDao or_chatDao;
    private final OR_ChanDao or_chanDao;
    private final OR_UserDao or_userDao;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();
    private final SocketService socketService = new SocketService();

    @Autowired
    public OR_DashService(OR_ChatDao or_chatDao, OR_ChanDao or_chanDao, OR_UserDao or_userDao) {
        this.or_chatDao = or_chatDao;
        this.or_chanDao = or_chanDao;
        this.or_userDao = or_userDao;
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
            return handleResults.handleResultByCode(200, result, "获取成功");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> getSystemOsInfo() throws IOException {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor processor = hal.getProcessor();
        InetAddress localHost = InetAddress.getLocalHost();

        return new HashMap<>(){{
            put("osInfo", System.getProperty("os.name"));
            put("osArch", System.getProperty("os.arch"));
            put("locale", LocaleContextHolder.getLocale());
            put("hostName", localHost.getHostName());
            put("appVersion", socketService.appVersion);
            put("timeZoneId", TimeZone.getDefault().getID());
            put("hostAddress", localHost.getHostAddress());
            put("systemUptime", new Application().getSystemUptime());
            put("logicalCount", processor.getLogicalProcessorCount());
            put("memoryUsageInfo", getSystemMemoryUsage());
        }};
    }

    private static String getSystemMemoryUsage() {
        GlobalMemory memory = new SystemInfo().getHardware().getMemory();
        long totalMemory = memory.getTotal(); // 总内存
        long availableMemory = memory.getAvailable(); // 可用内存
        long usedMemory = totalMemory - availableMemory; // 已使用内存
        return formatBytes(usedMemory) + "/" + formatBytes(totalMemory);
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1fKB", bytes / 1024.0);
        } else if (bytes < 1024L * 1024L * 1024L) {
            return String.format("%.1fMB", bytes / (1024.0 * 1024));
        } else return String.format("%.1fGB", bytes / (1024.0 * 1024 * 1024));
    }

    private <T> Long countRecordsInRange(BaseMapper<T> dao, SFunction<T, String> timeField, LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(timeField, start.format(formatter)).lt(timeField, end.format(formatter));
        return dao.selectCount(queryWrapper);
    }
}

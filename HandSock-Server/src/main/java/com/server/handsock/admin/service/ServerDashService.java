package com.server.handsock.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.server.handsock.admin.dao.ServerChannelDao;
import com.server.handsock.admin.dao.ServerChatDao;
import com.server.handsock.admin.dao.ServerUserDao;
import com.server.handsock.admin.mod.ServerChatModel;
import com.server.handsock.admin.mod.ServerUserModel;
import com.server.handsock.console.AppProperties;
import com.server.handsock.console.UtilityService;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
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
public class ServerDashService {

    private final ServerChatDao serverChatDao;
    private final ServerUserDao serverUserDao;
    private final AppProperties appProperties;
    private final ServerChannelDao serverChannelDao;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public ServerDashService(ServerChatDao serverChatDao, ServerChannelDao serverChannelDao, ServerUserDao serverUserDao, AppProperties appProperties) {
        this.serverChatDao = serverChatDao;
        this.serverUserDao = serverUserDao;
        this.appProperties = appProperties;
        this.serverChannelDao = serverChannelDao;
    }

    public Map<String, Object> getDashboardData() {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
            Long userTotal = serverUserDao.selectCount(null);
            Long chanTotal = serverChannelDao.selectCount(null);
            Long todayRegUser = countRecordsInRange(serverUserDao, ServerUserModel::getRegTime, startOfDay, endOfDay);
            Long todayChatTotal = countRecordsInRange(serverChatDao, ServerChatModel::getTime, startOfDay, endOfDay);

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
            put("appVersion", appProperties.getAppVersion());
            put("timeZoneId", TimeZone.getDefault().getID());
            put("hostAddress", localHost.getHostAddress());
            put("systemUptime", new UtilityService().getSystemUptime());
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

package com.server.handsock.utils

import oshi.SystemInfo
import java.lang.management.ManagementFactory

object SystemUtils {
    val systemUptime: String
        get() {
            val uptime = ManagementFactory.getRuntimeMXBean().uptime
            var seconds = uptime / 1000
            val days = seconds / 86400
            seconds %= 86400
            val hours = seconds / 3600
            seconds %= 3600
            val minutes = seconds / 60
            seconds %= 60
            return String.format("%d天%d小时%d分钟%d秒", days, hours, minutes, seconds)
        }

    val systemMemoryUsage: String
        get() {
            val memory = SystemInfo().hardware.memory
            val totalMemory = memory.total // 总内存
            val availableMemory = memory.available // 可用内存
            val usedMemory = totalMemory - availableMemory // 已使用内存
            return HandUtils.formatBytesForString(usedMemory) + "/" + HandUtils.formatBytesForString(totalMemory)
        }
}
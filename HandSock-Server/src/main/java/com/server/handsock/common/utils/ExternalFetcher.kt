package com.server.handsock.common.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.client.RestTemplate
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.math.min

object ExternalFetcher {
    private val restTemplate = RestTemplate()

    val hitokoto: String?
        get() {
            try {
                return restTemplate.getForObject(
                    "https://international.v1.hitokoto.cn/?encode=text",
                    String::class.java
                )
            } catch (e: Exception) {
                ConsoleUtils.printErrorLog(e)
                return "操作失败，请查看系统日志"
            }
        }

    val weiboHotSearch: String
        get() {
            try {
                val objectMapper = ObjectMapper()
                val result: Map<*, *>? = objectMapper.readValue(
                    restTemplate.getForObject(
                        "https://weibo.com/ajax/side/hotSearch",
                        String::class.java
                    ), Map::class.java
                )
                val dataMap = result?.get("data") as Map<*, *>?
                if (dataMap != null) {
                    @Suppress("UNCHECKED_CAST")
                    val data = dataMap["realtime"] as List<Map<String, Any>>?
                    return IntStream.range(0, min(data!!.size.toDouble(), 10.0).toInt())
                        .mapToObj { i: Int -> "${i + 1}、${data[i]["word"]}" }
                        .collect(Collectors.joining("<br/>"))
                }
            } catch (e: Exception) {
                ConsoleUtils.printErrorLog(e)
            }
            return "操作失败，请查看系统日志"
        }

    val bilibiliHotSearch: String
        get() {
            try {
                val objectMapper = ObjectMapper()
                val result: Map<*, *>? = objectMapper.readValue(
                    restTemplate.getForObject(
                        "https://api.bilibili.com/x/web-interface/wbi/search/square?limit=10",
                        String::class.java
                    ), Map::class.java
                )
                val dataMap = result?.get("data") as Map<*, *>?
                if (dataMap != null) {
                    val trending = dataMap["trending"] as Map<*, *>?
                    @Suppress("UNCHECKED_CAST")
                    val data = trending!!["list"] as List<Map<String, Any>>?
                    return IntStream.range(0, min(data!!.size.toDouble(), 10.0).toInt())
                        .mapToObj { i: Int -> "${i + 1}、${data[i]["keyword"]}" }
                        .collect(Collectors.joining("<br/>"))
                }
            } catch (e: Exception) {
                ConsoleUtils.printErrorLog(e)
            }
            return "操作失败，请查看系统日志"
        }
}
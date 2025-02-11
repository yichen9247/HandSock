package com.server.handsock.console;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExternalFetcher {

    private final RestTemplate restTemplate = new RestTemplate();
    protected final ConsolePrints consolePrints = new ConsolePrints();

    public String getHitokoto() {
        try {
            return restTemplate.getForObject("https://international.v1.hitokoto.cn/?encode=text", String.class);
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return "操作失败，请查看系统日志";
        }
    }

    public String getWeiboHotSearch() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(restTemplate.getForObject("https://weibo.com/ajax/side/hotSearch", String.class), Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) result.get("data");
            if (dataMap != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> data = (List<Map<String, Object>>) dataMap.get("realtime");
                return IntStream.range(0, Math.min(data.size(), 10))
                        .mapToObj(i -> (i + 1) + "、" + data.get(i).get("word"))
                        .collect(Collectors.joining("<br/>"));
            }
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
        }
        return "操作失败，请查看系统日志";
    }

    public String getBilibiliHotSearch() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(restTemplate.getForObject("https://api.bilibili.com/x/web-interface/wbi/search/square?limit=10", String.class), Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) result.get("data");
            if (dataMap != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> trending = (Map<String, Object>) dataMap.get("trending");
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> data = (List<Map<String, Object>>) trending.get("list");
                return IntStream.range(0, Math.min(data.size(), 10))
                        .mapToObj(i -> (i + 1) + "、" + data.get(i).get("keyword"))
                        .collect(Collectors.joining("<br/>"));
            }
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
        }
        return "操作失败，请查看系统日志";
    }
}

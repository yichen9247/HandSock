package com.server.handsock.console;

import java.util.Map;
import java.util.HashMap;

public class HandleResults {
    public Map<String, Object> handleResultByCode(int code, Object data, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        if (data != null) result.put("data", data);
        return result;
    }
}

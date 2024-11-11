package com.server.handsock.admin.service;

import org.springframework.stereotype.Service;
import com.server.handsock.admin.dao.OR_ChatDao;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.admin.mod.OR_ChatModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Service
public class OR_ChatService {

    private final OR_ChatDao or_chatDao;

    @Autowired
    public OR_ChatService(OR_ChatDao or_chatDao) {
        this.or_chatDao = or_chatDao;
    }

    public Map<String, Object> getChatList(int page, int limit) {
        try {
            List<OR_ChatModel> or_chatModelList = or_chatDao.selectList(null);
            int total = or_chatModelList.size();
            int startWith = (page - 1) * limit;
            int endWith = Math.min(startWith + limit, or_chatModelList.size());
            List<OR_ChatModel> subList = or_chatModelList.subList(startWith, endWith);
            return new HandleResults().handleResultByCode(200, new HashMap<>(){{
                put("items", subList);
                put("total", total);
            }}, "获取成功");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return new HandleResults().handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> deleteChat(String sid) {
        try {
            if (or_chatDao.deleteById(sid) > 0) {
                return new HandleResults().handleResultByCode(200, null, "删除成功");
            } else return new HandleResults().handleResultByCode(400, null, "删除失败");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return new HandleResults().handleResultByCode(500, null, "服务端异常");
        }
    }

    public void clearAllChatHistory() {
        try {
            if (or_chatDao.delete(null) > 0) {
                new HandleResults().handleResultByCode(200, null, "清空聊天记录成功");
            } else new HandleResults().handleResultByCode(400, null, "清空聊天记录成功失败");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            new HandleResults().handleResultByCode(500, null, "服务端异常");
        }
    }
}

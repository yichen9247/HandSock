package com.server.handsock.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import com.server.handsock.admin.dao.OR_ChatDao;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.admin.mod.OR_ChatModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Service
public class OR_ChatService {

    private final OR_ChatDao or_chatDao;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public OR_ChatService(OR_ChatDao or_chatDao) {
        this.or_chatDao = or_chatDao;
    }

    public Map<String, Object> getChatContent(String sid) {
        try {
            OR_ChatModel or_chatModel = or_chatDao.selectOne(new QueryWrapper<OR_ChatModel>().eq("sid", sid));
            if (or_chatModel != null) {
                return handleResults.handleResultByCode(200, or_chatModel, "获取成功");
            } else return handleResults.handleResultByCode(501, null, "内容已被删除");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> getChatList(int page, int limit) {
        try {
            List<OR_ChatModel> or_chatModelList = or_chatDao.selectList(null);
            int total = or_chatModelList.size();
            int startWith = (page - 1) * limit;
            Collections.reverse(or_chatModelList);
            int endWith = Math.min(startWith + limit, or_chatModelList.size());
            List<OR_ChatModel> subList = or_chatModelList.subList(startWith, endWith);
            return handleResults.handleResultByCode(200, new HashMap<>(){{
                put("items", subList);
                put("total", total);
            }}, "获取成功");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> deleteChat(String sid) {
        try {
            OR_ChatModel or_chatModel = or_chatDao.selectOne(new QueryWrapper<OR_ChatModel>().eq("sid", sid));
            if (or_chatModel != null) {
                if (or_chatDao.deleteById(sid) > 0) {
                    return handleResults.handleResultByCode(200, null, "删除成功");
                } else return handleResults.handleResultByCode(400, null, "删除失败");
            } else return handleResults.handleResultByCode(501, null, "消息已被删除");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public void clearAllChatHistory() {
        try {
            if (or_chatDao.delete(null) > 0) {
                handleResults.handleResultByCode(200, null, "清空聊天记录成功");
            } else new HandleResults().handleResultByCode(400, null, "清空聊天记录成功失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }
}

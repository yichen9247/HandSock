package com.server.handsock.sockets.service;

import org.springframework.stereotype.Service;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.sockets.dao.OP_ChatDao;
import com.server.handsock.sockets.mod.OP_ChatModel;
import com.server.handsock.sockets.man.OP_ChatManage;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.Map;

@Service
public class OP_ChatService {

    private final OP_ChatDao opChatDao;
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public OP_ChatService(OP_ChatDao opChatDao) {
        this.opChatDao = opChatDao;
    }

    public OP_ChatModel searchHistoryById(String sid) {
        try {
            return opChatDao.selectOne(new QueryWrapper<OP_ChatModel>().eq("sid", sid));
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            new HandleResults().handleResultByCode(500, null, "服务端异常");
        }
        return null;
    }

    public Map<String, Object> insertChatMessage(String type, long uid, long gid, String address, String content) {
        try {
            OP_ChatModel OPChatModel = new OP_ChatModel();
            Map<String, Object> result = new OP_ChatManage().insertChatMessage(OPChatModel, type, uid, gid, address, content);
            if (opChatDao.insert(OPChatModel) > 0) {
                return handleResults.handleResultByCode(200, result, "发送成功");
            } else return handleResults.handleResultByCode(400, null, "发送失败");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> searchAllChatHistory(Long gid) {
        try {
            QueryWrapper<OP_ChatModel> wrapper = new QueryWrapper<>();
            wrapper.orderByAsc("time");
            return handleResults.handleResultByCode(200, opChatDao.selectList(wrapper.eq("gid", gid)), "获取成功");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }
}

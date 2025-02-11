package com.server.handsock.clients.service;

import org.springframework.stereotype.Service;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.clients.dao.ClientChatDao;
import com.server.handsock.clients.mod.ClientChatModel;
import com.server.handsock.clients.man.ClientChatManage;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.Map;

@Service
public class ClientChatService {

    private final ClientChatDao clientChatDao;
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public ClientChatService(ClientChatDao clientChatDao) {
        this.clientChatDao = clientChatDao;
    }

    public ClientChatModel searchHistoryById(String sid) {
        try {
            return clientChatDao.selectOne(new QueryWrapper<ClientChatModel>().eq("sid", sid));
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            new HandleResults().handleResultByCode(500, null, "服务端异常");
        }
        return null;
    }

    public Map<String, Object> insertChatMessage(String type, long uid, long gid, String address, String content) {
        try {
            ClientChatModel OPChatModel = new ClientChatModel();
            Map<String, Object> result = new ClientChatManage().insertChatMessage(OPChatModel, type, uid, gid, address, content);
            if (clientChatDao.insert(OPChatModel) > 0) {
                return handleResults.handleResultByCode(200, result, "发送成功");
            } else return handleResults.handleResultByCode(400, null, "发送失败");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> searchAllChatHistory(Long gid) {
        try {
            QueryWrapper<ClientChatModel> wrapper = new QueryWrapper<>();
            wrapper.orderByAsc("time");
            return handleResults.handleResultByCode(200, clientChatDao.selectList(wrapper.eq("gid", gid)), "获取成功");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }
}

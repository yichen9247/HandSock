package com.server.handsock.clients.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.services.HistoryService;
import com.server.handsock.clients.dao.ClientChannelDao;
import com.server.handsock.clients.mod.ClientChannelModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClientChannelService {

    private final HistoryService historyService;
    private final ClientChannelDao clientChannelDao;
    private final ClientChatService clientChatService;
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public ClientChannelService(ClientChannelDao clientChannelDao, HistoryService historyService, ClientChatService clientChatService) {
        this.historyService = historyService;
        this.clientChannelDao = clientChannelDao;
        this.clientChatService = clientChatService;
    }

    public Map<String, Object> searchAllGroup() {
        try {
            List<ClientChannelModel> OPChanModelList = clientChannelDao.selectList(new QueryWrapper<ClientChannelModel>().eq("active", 1));

            List<Map<String, Object>> history = new ArrayList<>();
            for (ClientChannelModel OPChanModel : OPChanModelList) {
                history.add(new HashMap<>(){{
                    put("gid", OPChanModel.getGid());
                    put("latest", clientChatService.searchHistoryById(historyService.queryGroupLeastHistory(OPChanModel.getGid())));
                }});
            }
            Map<String, Object> result = new HandleResults().handleResultByCode(200, OPChanModelList, "获取成功");
            result.put("latest", history);
            return result;
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> searchGroupByUid(long gid) {
        try {
            ClientChannelModel groupSelect;
            if (gid == 0) {
                groupSelect = clientChannelDao.selectOne(new QueryWrapper<ClientChannelModel>().eq("home", 1));
            } else groupSelect = clientChannelDao.selectOne(new QueryWrapper<ClientChannelModel>().eq("gid", gid));
            if (groupSelect == null) {
                return handleResults.handleResultByCode(404, null, "未找到频道");
            } else return handleResults.handleResultByCode(200, groupSelect, "获取成功");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Boolean getChanOpenStatus(Long gid) {
        try {
            Integer status = clientChannelDao.selectOne(new QueryWrapper<ClientChannelModel>().eq("gid", gid)).getOpen();
            return Objects.equals(status, 1);
        } catch (Exception e) {
            new ConsolePrints().printSuccessLog(e);
            return false;
        }
    }
}

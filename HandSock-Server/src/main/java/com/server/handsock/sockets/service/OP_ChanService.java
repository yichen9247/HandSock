package com.server.handsock.sockets.service;

import org.springframework.stereotype.Service;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.sockets.dao.OP_ChanDao;
import com.server.handsock.service.HistoryService;
import com.server.handsock.sockets.mod.OP_ChanModel;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class OP_ChanService {

    private final OP_ChanDao opChanDao;
    private final OP_ChatService opChatService;
    private final HistoryService historyService;
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public OP_ChanService(OP_ChanDao opChanDao, HistoryService historyService, OP_ChatService opChatService) {
        this.opChanDao = opChanDao;
        this.opChatService = opChatService;
        this.historyService = historyService;
    }

    public Map<String, Object> searchAllGroup() {
        try {
            List<OP_ChanModel> OPChanModelList = opChanDao.selectList(new QueryWrapper<OP_ChanModel>().eq("active", 1));

            List<Map<String, Object>> history = new ArrayList<>();
            for (OP_ChanModel OPChanModel : OPChanModelList) {
                history.add(new HashMap<>(){{
                    put("gid", OPChanModel.getGid());
                    put("latest", opChatService.searchHistoryById(historyService.queryGroupLeastHistory(OPChanModel.getGid())));
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
            OP_ChanModel groupSelect;
            if (gid == 0) {
                groupSelect = opChanDao.selectOne(new QueryWrapper<OP_ChanModel>().eq("home", 1));
            } else groupSelect = opChanDao.selectOne(new QueryWrapper<OP_ChanModel>().eq("gid", gid));
            if (groupSelect == null) {
                return handleResults.handleResultByCode(404, null, "未找到频道");
            } else return handleResults.handleResultByCode(200, groupSelect, "获取成功");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }
}

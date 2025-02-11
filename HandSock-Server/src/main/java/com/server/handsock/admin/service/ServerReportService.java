package com.server.handsock.admin.service;

import org.springframework.stereotype.Service;
import com.server.handsock.admin.dao.ServerReportDao;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.admin.mod.ServerReportModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServerReportService {

    private final ServerReportDao serverReportDao;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public ServerReportService(ServerReportDao serverReportDao) {
        this.serverReportDao = serverReportDao;
    }

    public Map<String, Object> getReportList(int page, int limit) {
        try {
            List<ServerReportModel> serverReportModelList = serverReportDao.selectList(null);
            int total = serverReportModelList.size();
            int startWith = (page - 1) * limit;
            Collections.reverse(serverReportModelList);
            int endWith = Math.min(startWith + limit, serverReportModelList.size());
            List<ServerReportModel> subList = serverReportModelList.subList(startWith, endWith);
            return handleResults.handleResultByCode(200, new HashMap<>(){{
                put("items", subList);
                put("total", total);
            }}, "获取成功");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> deleteReport(String sid) {
        try {
            if (serverReportDao.deleteById(sid) > 0) {
                return handleResults.handleResultByCode(200, null, "删除成功");
            } else return handleResults.handleResultByCode(400, null, "删除失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }
}

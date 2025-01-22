package com.server.handsock.admin.service;

import org.springframework.stereotype.Service;
import com.server.handsock.admin.dao.OR_RepoDao;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.admin.mod.OR_RepoModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OR_RepoService {

    private final OR_RepoDao or_repoDao;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public OR_RepoService(OR_RepoDao or_repoDao) {
        this.or_repoDao = or_repoDao;
    }

    public Map<String, Object> getReportList(int page, int limit) {
        try {
            List<OR_RepoModel> or_repoModelList = or_repoDao.selectList(null);
            int total = or_repoModelList.size();
            int startWith = (page - 1) * limit;
            Collections.reverse(or_repoModelList);
            int endWith = Math.min(startWith + limit, or_repoModelList.size());
            List<OR_RepoModel> subList = or_repoModelList.subList(startWith, endWith);
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
            if (or_repoDao.deleteById(sid) > 0) {
                return handleResults.handleResultByCode(200, null, "删除成功");
            } else return handleResults.handleResultByCode(400, null, "删除失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }
}

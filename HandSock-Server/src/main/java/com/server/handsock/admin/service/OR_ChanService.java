package com.server.handsock.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.server.handsock.admin.dao.OR_ChanDao;
import com.server.handsock.admin.dao.OR_ChatDao;
import com.server.handsock.admin.man.OR_ChanManage;
import com.server.handsock.admin.mod.OR_ChanModel;
import com.server.handsock.admin.mod.OR_ChatModel;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OR_ChanService {

    private final OR_ChanDao or_chanDao;
    private final OR_ChatDao or_chatDao;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public OR_ChanService(OR_ChanDao or_chanDao, OR_ChatDao or_chatDao) {
        this.or_chanDao = or_chanDao;
        this.or_chatDao = or_chatDao;
    }

    public Map<String, Object> getChanList(int page, int limit) {
        try {
            List<OR_ChanModel> or_chanModelList = or_chanDao.selectList(null);
            int total = or_chanModelList.size();
            int startWith = (page - 1) * limit;
            Collections.reverse(or_chanModelList);
            int endWith = Math.min(startWith + limit, or_chanModelList.size());
            List<OR_ChanModel> subList = or_chanModelList.subList(startWith, endWith);
            return handleResults.handleResultByCode(200, new HashMap<>(){{
                put("items", subList);
                put("total", total);
            }}, "获取成功");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> deleteChan(Long gid) {
        if (gid == 0) return handleResults.handleResultByCode(409, null, "主频道不可操作");
        try {
            List<OR_ChatModel> or_chatModels = or_chatDao.selectList(new QueryWrapper<OR_ChatModel>().eq("gid", gid));
            if (or_chatModels.isEmpty()) {
                if (or_chanDao.deleteById(gid) > 0) return handleResults.handleResultByCode(200, null, "删除成功");
            } else {
                if (or_chanDao.deleteById(gid) > 0 && or_chatDao.delete(new QueryWrapper<OR_ChatModel>().eq("gid", gid)) > 0) return handleResults.handleResultByCode(200, null, "删除成功");
            }
            return handleResults.handleResultByCode(400, null, "删除失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> updateChan(Long gid, String name, String avatar, String notice) {
        try {
            if (or_chanDao.selectOne(new QueryWrapper<OR_ChanModel>().eq("gid", gid)) == null) return handleResults.handleResultByCode(409, null, "频道不存在");
            OR_ChanModel or_chanModel = new OR_ChanModel();
            new OR_ChanManage().setChan(or_chanModel, gid, name, avatar, notice);
            if (or_chanDao.updateById(or_chanModel) > 0) {
                return handleResults.handleResultByCode(200, null, "修改成功");
            } else return handleResults.handleResultByCode(400, null, "修改失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> createChan(Long gid, String name, String avatar, String notice) {
        try {
            if (or_chanDao.selectOne(new QueryWrapper<OR_ChanModel>().eq("gid", gid)) != null) return handleResults.handleResultByCode(409, null, "频道已存在");
            OR_ChanModel or_chanModel = new OR_ChanModel();
            new OR_ChanManage().setChan(or_chanModel, gid, name, avatar, notice);
            if (or_chanDao.insert(or_chanModel) > 0) {
                return handleResults.handleResultByCode(200, null, "创建成功");
            } else return handleResults.handleResultByCode(400, null, "创建失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> updateChanOpenStatus(Long gid, Integer status) {
        if (gid == 0) return handleResults.handleResultByCode(409, null, "主频道不可操作");
        try {
            OR_ChanModel or_chanModel = new OR_ChanModel();
            new OR_ChanManage().updateChanOpenStatus(or_chanModel, gid, status);
            if (or_chanDao.updateById(or_chanModel) > 0) {
                return handleResults.handleResultByCode(200, null, "设置成功");
            } else return handleResults.handleResultByCode(400, null, "设置失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> updateChanActiveStatus(Long gid, Integer status) {
        if (gid == 0) return handleResults.handleResultByCode(409, null, "主频道不可操作");
        try {
            OR_ChanModel or_chanModel = new OR_ChanModel();
            new OR_ChanManage().updateChanActiveStatus(or_chanModel, gid, status);
            if (or_chanDao.updateById(or_chanModel) > 0) {
                return handleResults.handleResultByCode(200, null, "设置成功");
            } else return handleResults.handleResultByCode(400, null, "设置失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Boolean getChanOpenStatus(Long gid) {
        try {
            Integer status = or_chanDao.selectOne(new QueryWrapper<OR_ChanModel>().eq("gid", gid)).getOpen();
            return Objects.equals(status, 1);
        } catch (Exception e) {
            new ConsolePrints().printSuccessLog(e);
            return false;
        }
    }
}

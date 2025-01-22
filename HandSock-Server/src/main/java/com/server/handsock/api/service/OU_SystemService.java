package com.server.handsock.api.service;

import org.springframework.stereotype.Service;
import com.server.handsock.api.dao.OU_SystemDao;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.api.mod.OU_SystemModel;
import com.server.handsock.api.man.OU_SystemManage;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.Map;
import java.util.List;
import java.util.Objects;

@Service
public class OU_SystemService {

    private final OU_SystemDao OUSystemDao;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public OU_SystemService(OU_SystemDao OUSystemDao) {
        this.OUSystemDao = OUSystemDao;
    }

    public Boolean getSystemKeyStatus(String key) {
        try {
            OU_SystemModel OUSystemModel = OUSystemDao.selectOne(new QueryWrapper<OU_SystemModel>().eq("name", key));
            if (OUSystemModel != null) return Objects.equals(OUSystemModel.getValue(), "open");
            return false;
        } catch (Exception e) {
            new ConsolePrints().printSuccessLog(e);
            return false;
        }
    }

    public Map<String, Object> getSystemKeyConfig(String key) {
        OU_SystemModel OUSystemModel = OUSystemDao.selectOne(new QueryWrapper<OU_SystemModel>().eq("name", key));
        return handleResults.handleResultByCode(200, OUSystemModel.getValue(), "获取成功");
    }

    public Map<String, Object> setSystemTabooStatus(String value) {
        try {
            OU_SystemModel OUSystemModel = OUSystemDao.selectOne(new QueryWrapper<OU_SystemModel>().eq("name", "taboo"));
            Map<String, Object> result = new OU_SystemManage().setSystemKeyStatus(OUSystemModel, value);
            if (OUSystemDao.updateById(OUSystemModel) > 0) {
                return handleResults.handleResultByCode(200, result, "设置状态成功");
            } else return handleResults.handleResultByCode(200, null, "设置状态失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> setSystemUploadStatus(String value) {
        try {
            OU_SystemModel OUSystemModel = OUSystemDao.selectOne(new QueryWrapper<OU_SystemModel>().eq("name", "upload"));
            Map<String, Object> result = new OU_SystemManage().setSystemKeyStatus(OUSystemModel, value);
            if (OUSystemDao.updateById(OUSystemModel) > 0) {
                return handleResults.handleResultByCode(200, result, "设置状态成功");
            } else return handleResults.handleResultByCode(200, null, "设置状态失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> setSystemPlaylistValue(String value) {
        try {
            OU_SystemModel OUSystemModel = OUSystemDao.selectOne(new QueryWrapper<OU_SystemModel>().eq("name", "playlist"));
            Map<String, Object> result = new OU_SystemManage().setSystemKeyStatus(OUSystemModel, value);
            if (OUSystemDao.updateById(OUSystemModel) > 0) {
                return handleResults.handleResultByCode(200, result, "设置音乐成功");
            } else return handleResults.handleResultByCode(200, null, "设置音乐失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> getAllSystemConfig() {
        try {
            List<OU_SystemModel> OUSystemModelList = OUSystemDao.selectList(null);
            return handleResults.handleResultByCode(200, OUSystemModelList, "获取成功");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }
}

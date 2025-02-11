package com.server.handsock.admin.service;

import org.springframework.stereotype.Service;
import com.server.handsock.admin.dao.ServerSystemDao;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.admin.mod.ServerSystemModel;
import com.server.handsock.admin.man.ServerSystemManage;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.Map;
import java.util.List;
import java.util.Objects;

@Service
public class ServerSystemService {

    private final ServerSystemDao serverSystemDao;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public ServerSystemService(ServerSystemDao serverSystemDao) {
        this.serverSystemDao = serverSystemDao;
    }

    public Boolean getSystemKeyStatus(String key) {
        try {
            ServerSystemModel serverSystemModel = serverSystemDao.selectOne(new QueryWrapper<ServerSystemModel>().eq("name", key));
            if (serverSystemModel != null) return Objects.equals(serverSystemModel.getValue(), "open");
            return false;
        } catch (Exception e) {
            new ConsolePrints().printSuccessLog(e);
            return false;
        }
    }

    public Map<String, Object> getSystemKeyConfig(String key) {
        ServerSystemModel serverSystemModel = serverSystemDao.selectOne(new QueryWrapper<ServerSystemModel>().eq("name", key));
        return handleResults.handleResultByCode(200, serverSystemModel.getValue(), "获取成功");
    }

    public Map<String, Object> setSystemTabooStatus(String value) {
        try {
            ServerSystemModel serverSystemModel = serverSystemDao.selectOne(new QueryWrapper<ServerSystemModel>().eq("name", "taboo"));
            Map<String, Object> result = new ServerSystemManage().setSystemKeyStatus(serverSystemModel, value);
            if (serverSystemDao.updateById(serverSystemModel) > 0) {
                return handleResults.handleResultByCode(200, result, "设置状态成功");
            } else return handleResults.handleResultByCode(200, null, "设置状态失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> setSystemUploadStatus(String value) {
        try {
            ServerSystemModel serverSystemModel = serverSystemDao.selectOne(new QueryWrapper<ServerSystemModel>().eq("name", "upload"));
            Map<String, Object> result = new ServerSystemManage().setSystemKeyStatus(serverSystemModel, value);
            if (serverSystemDao.updateById(serverSystemModel) > 0) {
                return handleResults.handleResultByCode(200, result, "设置状态成功");
            } else return handleResults.handleResultByCode(200, null, "设置状态失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> setSystemRegisterStatus(String value) {
        try {
            ServerSystemModel serverSystemModel = serverSystemDao.selectOne(new QueryWrapper<ServerSystemModel>().eq("name", "register"));
            Map<String, Object> result = new ServerSystemManage().setSystemKeyStatus(serverSystemModel, value);
            if (serverSystemDao.updateById(serverSystemModel) > 0) {
                return handleResults.handleResultByCode(200, result, "设置状态成功");
            } else return handleResults.handleResultByCode(200, null, "设置状态失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> setSystemPlaylistValue(String value) {
        try {
            ServerSystemModel serverSystemModel = serverSystemDao.selectOne(new QueryWrapper<ServerSystemModel>().eq("name", "playlist"));
            Map<String, Object> result = new ServerSystemManage().setSystemKeyStatus(serverSystemModel, value);
            if (serverSystemDao.updateById(serverSystemModel) > 0) {
                return handleResults.handleResultByCode(200, result, "设置音乐成功");
            } else return handleResults.handleResultByCode(200, null, "设置音乐失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> getAllSystemConfig() {
        try {
            List<ServerSystemModel> serverSystemModelList = serverSystemDao.selectList(null);
            return handleResults.handleResultByCode(200, serverSystemModelList, "获取成功");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }
}

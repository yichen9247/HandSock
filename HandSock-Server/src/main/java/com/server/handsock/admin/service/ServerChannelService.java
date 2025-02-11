package com.server.handsock.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.server.handsock.admin.dao.ServerChannelDao;
import com.server.handsock.admin.dao.ServerChatDao;
import com.server.handsock.admin.man.ServerChannelManage;
import com.server.handsock.admin.mod.ServerChannelModel;
import com.server.handsock.admin.mod.ServerChatModel;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServerChannelService {

    private final ServerChatDao serverChatDao;
    private final ServerChannelDao serverChannelDao;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public ServerChannelService(ServerChannelDao serverChannelDao, ServerChatDao serverChatDao) {
        this.serverChatDao = serverChatDao;
        this.serverChannelDao = serverChannelDao;
    }

    public Map<String, Object> getChanList(int page, int limit) {
        try {
            List<ServerChannelModel> serverChannelModelList = serverChannelDao.selectList(null);
            int total = serverChannelModelList.size();
            int startWith = (page - 1) * limit;
            Collections.reverse(serverChannelModelList);
            int endWith = Math.min(startWith + limit, serverChannelModelList.size());
            List<ServerChannelModel> subList = serverChannelModelList.subList(startWith, endWith);
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
            List<ServerChatModel> server_chatModels = serverChatDao.selectList(new QueryWrapper<ServerChatModel>().eq("gid", gid));
            if (server_chatModels.isEmpty()) {
                if (serverChannelDao.deleteById(gid) > 0) return handleResults.handleResultByCode(200, null, "删除成功");
            } else {
                if (serverChannelDao.deleteById(gid) > 0 && serverChatDao.delete(new QueryWrapper<ServerChatModel>().eq("gid", gid)) > 0) return handleResults.handleResultByCode(200, null, "删除成功");
            }
            return handleResults.handleResultByCode(400, null, "删除失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> updateChan(Long gid, String name, String avatar, String notice, Boolean aiRole) {
        try {
            if (serverChannelDao.selectOne(new QueryWrapper<ServerChannelModel>().eq("gid", gid)) == null) return handleResults.handleResultByCode(409, null, "频道不存在");
            ServerChannelModel serverChannelModel = new ServerChannelModel();
            new ServerChannelManage().setChan(serverChannelModel, gid, name, avatar, notice, aiRole);
            if (serverChannelDao.updateById(serverChannelModel) > 0) {
                return handleResults.handleResultByCode(200, null, "修改成功");
            } else return handleResults.handleResultByCode(400, null, "修改失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> createChan(Long gid, String name, String avatar, String notice, Boolean aiRole) {
        try {
            if (serverChannelDao.selectOne(new QueryWrapper<ServerChannelModel>().eq("gid", gid)) != null) return handleResults.handleResultByCode(409, null, "频道已存在");
            ServerChannelModel serverChannelModel = new ServerChannelModel();
            new ServerChannelManage().setChan(serverChannelModel, gid, name, avatar, notice, aiRole);
            if (serverChannelDao.insert(serverChannelModel) > 0) {
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
            ServerChannelModel serverChannelModel = new ServerChannelModel();
            new ServerChannelManage().updateChanOpenStatus(serverChannelModel, gid, status);
            if (serverChannelDao.updateById(serverChannelModel) > 0) {
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
            ServerChannelModel serverChannelModel = new ServerChannelModel();
            new ServerChannelManage().updateChanActiveStatus(serverChannelModel, gid, status);
            if (serverChannelDao.updateById(serverChannelModel) > 0) {
                return handleResults.handleResultByCode(200, null, "设置成功");
            } else return handleResults.handleResultByCode(400, null, "设置失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }
}

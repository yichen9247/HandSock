package com.server.handsock.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.server.handsock.admin.dao.ServerChatDao;
import com.server.handsock.admin.dao.ServerUserDao;
import com.server.handsock.admin.man.ServerUserManage;
import com.server.handsock.admin.mod.ServerChatModel;
import com.server.handsock.admin.mod.ServerUserModel;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.clients.dao.ClientUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServerUserService {

    private final ServerUserDao serverUserDao;
    private final ServerChatDao serverChatDao;
    private final ClientUserDao clientUserDao;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public ServerUserService(ServerUserDao serverUserDao, ServerChatDao serverChatDao, ClientUserDao clientUserDao) {
        this.serverUserDao = serverUserDao;
        this.serverChatDao = serverChatDao;
        this.clientUserDao = clientUserDao;
    }

    public Map<String, Object> getUserList(int page, int limit) {
        try {
            List<ServerUserModel> server_userModelList = serverUserDao.selectList(null);
            int startWith = (page - 1) * limit;
            Collections.reverse(server_userModelList);
            int endWith = Math.min(startWith + limit, server_userModelList.size());
            List<ServerUserModel> subList = server_userModelList.subList(startWith, endWith);
            return handleResults.handleResultByCode(200, new HashMap<>(){{
                put("items", subList);
                put("total", server_userModelList.size());
            }}, "获取成功");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> deleteUser(Long uid) {
        try {
            List<ServerChatModel> server_chatModels = serverChatDao.selectList(new QueryWrapper<ServerChatModel>().eq("uid", uid));
            if (clientUserDao.selectById(uid).getIsAdmin() == 1) return handleResults.handleResultByCode(409, null, "无法操作管理员账号");
            if (server_chatModels.isEmpty()) {
                if (serverUserDao.deleteById(uid) > 0) return handleResults.handleResultByCode(200, null, "删除成功");
            } else {
                if (serverUserDao.deleteById(uid) > 0 && serverChatDao.delete(new QueryWrapper<ServerChatModel>().eq("uid", uid)) > 0) return handleResults.handleResultByCode(200, null, "删除成功");
            }
            return handleResults.handleResultByCode(400, null, "删除失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> updateUserPassword(Long uid, String password) {
        try {
            ServerUserModel server_userModel = new ServerUserModel();
            new ServerUserManage().updateUserPassword(server_userModel, uid, password);
            if (serverUserDao.updateById(server_userModel) > 0) {
                return handleResults.handleResultByCode(200, null, "修改成功");
            } else return handleResults.handleResultByCode(400, null, "修改失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> updateUserTabooStatus(Long uid, String status) {
        try {
            ServerUserModel server_userModel = new ServerUserModel();
            new ServerUserManage().updateUserTabooStatus(server_userModel, uid, status);
            if (serverUserDao.updateById(server_userModel) > 0) {
                return handleResults.handleResultByCode(200, null, "设置成功");
            } else return handleResults.handleResultByCode(400, null, "设置失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> updateUserInfo(Long uid, String username, String nick, String avatar, Boolean robot) {
        try {
            if (serverUserDao.selectOne(new QueryWrapper<ServerUserModel>().eq("uid", uid)) == null) return handleResults.handleResultByCode(409, null, "用户不存在");
            ServerUserModel server_userModel = new ServerUserModel();
            ServerUserModel or_robotModel = serverUserDao.selectOne(new QueryWrapper<ServerUserModel>().eq("is_robot", 1));
            if (robot) {
                if (or_robotModel != null) {
                    or_robotModel.setIsRobot(0);
                    new ServerUserManage().updateUserInfo(server_userModel, uid, username, nick, avatar, 1);
                    if (serverUserDao.updateById(or_robotModel) > 0 && serverUserDao.updateById(server_userModel) > 0) return handleResults.handleResultByCode(200, null, "修改成功");
                } else {
                    new ServerUserManage().updateUserInfo(server_userModel, uid, username, nick, avatar, 1);
                    if (serverUserDao.updateById(server_userModel) > 0) return handleResults.handleResultByCode(200, null, "修改成功");
                }
            } else {
                new ServerUserManage().updateUserInfo(server_userModel, uid, username, nick, avatar, 0);
                if (serverUserDao.updateById(server_userModel) > 0) return handleResults.handleResultByCode(200, null, "修改成功");
            }
            return handleResults.handleResultByCode(400, null, "修改失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }
}

package com.server.handsock.admin.service;

import com.server.handsock.sockets.dao.OP_UserDao;
import org.springframework.stereotype.Service;
import com.server.handsock.admin.dao.OR_ChatDao;
import com.server.handsock.admin.dao.OR_UserDao;
import com.server.handsock.console.HandleResults;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.admin.mod.OR_ChatModel;
import com.server.handsock.admin.mod.OR_UserModel;
import com.server.handsock.admin.man.OR_UserManage;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.*;

@Service
public class OR_UserService {

    private final OR_UserDao or_userDao;
    private final OR_ChatDao or_chatDao;
    private final OP_UserDao op_userDao;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public OR_UserService(OR_UserDao or_userDao, OR_ChatDao or_chatDao, OP_UserDao op_userDao) {
        this.or_userDao = or_userDao;
        this.or_chatDao = or_chatDao;
        this.op_userDao = op_userDao;
    }

    public Map<String, Object> getUserList(int page, int limit) {
        try {
            List<OR_UserModel> or_userModelList = or_userDao.selectList(null);
            int startWith = (page - 1) * limit;
            Collections.reverse(or_userModelList);
            int endWith = Math.min(startWith + limit, or_userModelList.size());
            List<OR_UserModel> subList = or_userModelList.subList(startWith, endWith);
            return handleResults.handleResultByCode(200, new HashMap<>(){{
                put("items", subList);
                put("total", or_userModelList.size());
            }}, "获取成功");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> deleteUser(Long uid) {
        try {
            List<OR_ChatModel> or_chatModels = or_chatDao.selectList(new QueryWrapper<OR_ChatModel>().eq("uid", uid));
            if (op_userDao.selectById(uid).getIsAdmin() == 1) return handleResults.handleResultByCode(409, null, "无法操作管理员账号");
            if (or_chatModels.isEmpty()) {
                if (or_userDao.deleteById(uid) > 0) return handleResults.handleResultByCode(200, null, "删除成功");
            } else {
                if (or_userDao.deleteById(uid) > 0 && or_chatDao.delete(new QueryWrapper<OR_ChatModel>().eq("uid", uid)) > 0) return handleResults.handleResultByCode(200, null, "删除成功");
            }
            return handleResults.handleResultByCode(400, null, "删除失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> updateUserPassword(Long uid, String password) {
        try {
            OR_UserModel or_userModel = new OR_UserModel();
            new OR_UserManage().updateUserPassword(or_userModel, uid, password);
            if (or_userDao.updateById(or_userModel) > 0) {
                return handleResults.handleResultByCode(200, null, "修改成功");
            } else return handleResults.handleResultByCode(400, null, "修改失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> updateUserTabooStatus(Long uid, String status) {
        try {
            OR_UserModel or_userModel = new OR_UserModel();
            new OR_UserManage().updateUserTabooStatus(or_userModel, uid, status);
            if (or_userDao.updateById(or_userModel) > 0) {
                return handleResults.handleResultByCode(200, null, "设置成功");
            } else return handleResults.handleResultByCode(400, null, "设置失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> updateUserInfo(Long uid, String username, String nick, String avatar, Boolean robot) {
        try {
            if (or_userDao.selectOne(new QueryWrapper<OR_UserModel>().eq("uid", uid)) == null) return handleResults.handleResultByCode(409, null, "用户不存在");
            OR_UserModel or_userModel = new OR_UserModel();
            OR_UserModel or_robotModel = or_userDao.selectOne(new QueryWrapper<OR_UserModel>().eq("is_robot", 1));
            if (robot) {
                if (or_robotModel != null) {
                    or_robotModel.setIsRobot(0);
                    new OR_UserManage().updateUserInfo(or_userModel, uid, username, nick, avatar, 1);
                    if (or_userDao.updateById(or_robotModel) > 0 && or_userDao.updateById(or_userModel) > 0) return handleResults.handleResultByCode(200, null, "修改成功");
                } else {
                    new OR_UserManage().updateUserInfo(or_userModel, uid, username, nick, avatar, 1);
                    if (or_userDao.updateById(or_userModel) > 0) return handleResults.handleResultByCode(200, null, "修改成功");
                }
            } else {
                new OR_UserManage().updateUserInfo(or_userModel, uid, username, nick, avatar, 0);
                if (or_userDao.updateById(or_userModel) > 0) return handleResults.handleResultByCode(200, null, "修改成功");
            }
            return handleResults.handleResultByCode(400, null, "修改失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Boolean getUserInnerStatus(Long uid) {
        try {
            OR_UserModel or_userModel = or_userDao.selectOne(new QueryWrapper<OR_UserModel>().eq("uid", uid));
            return or_userModel != null;
        } catch (Exception e) {
            new ConsolePrints().printSuccessLog(e);
            return false;
        }
    }

    public Boolean getUserTabooStatus(Long uid) {
        try {
            String status = or_userDao.selectOne(new QueryWrapper<OR_UserModel>().eq("uid", uid)).getTaboo();
            return Objects.equals(status, "open");
        } catch (Exception e) {
            new ConsolePrints().printSuccessLog(e);
            return false;
        }
    }

    public Long getRobotInnerStatus() {
        try {
            OR_UserModel or_userModel = or_userDao.selectOne(new QueryWrapper<OR_UserModel>().eq("is_robot", 1));
            if (or_userModel != null) {
                return or_userModel.getUid();
            } else return null;
        } catch (Exception e) {
            new ConsolePrints().printSuccessLog(e);
            return null;
        }
    }
}

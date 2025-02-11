package com.server.handsock.clients.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.server.handsock.admin.dao.ServerUserDao;
import com.server.handsock.admin.mod.ServerUserModel;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.console.UtilityService;
import com.server.handsock.console.IDGenerator;
import com.server.handsock.services.TokenService;
import com.server.handsock.clients.dao.ClientUserDao;
import com.server.handsock.clients.man.ClientUserManage;
import com.server.handsock.clients.mod.ClientUserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class ClientUserService {

    private final UtilityService utilityService = new UtilityService();
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]+$";
    private static final String PASSWORD_PATTERN = "^[a-zA-Z0-9]+$";

    private final TokenService tokenService;
    private final ClientUserDao clientUserDao;
    private final ServerUserDao server_userDao;

    @Autowired
    public ClientUserService(ServerUserDao server_userDao, ClientUserDao clientUserDao, TokenService tokenService) {
        this.server_userDao = server_userDao;
        this.tokenService = tokenService;
        this.clientUserDao = clientUserDao;
    }

    public ClientUserModel queryUserInfo(long uid) {
        return clientUserDao.selectById(uid);
    }

    public Map<String, Object> queryAllUser() {
        try {
            return handleResult(200, clientUserDao.selectList(null), "获取成功");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResult(500, null, "服务端异常");
        }
    }

    public Boolean checkUserLogin(long uid) {
        try {
            return clientUserDao.selectOne(new QueryWrapper<ClientUserModel>().eq("uid", uid)) != null;
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return false;
        }
    }

    public Boolean getUserInnerStatus(Long uid) {
        try {
            ClientUserModel selectResult = clientUserDao.selectOne(new QueryWrapper<ClientUserModel>().eq("uid", uid));
            return selectResult != null;
        } catch (Exception e) {
            new ConsolePrints().printSuccessLog(e);
            return false;
        }
    }

    public Boolean getUserTabooStatus(Long uid) {
        try {
            String status = clientUserDao.selectOne(new QueryWrapper<ClientUserModel>().eq("uid", uid)).getTaboo();
            return Objects.equals(status, "open");
        } catch (Exception e) {
            new ConsolePrints().printSuccessLog(e);
            return false;
        }
    }

    public Long getRobotInnerStatus() {
        try {
            ClientUserModel selectResult = clientUserDao.selectOne(new QueryWrapper<ClientUserModel>().eq("is_robot", 1));
            if (selectResult != null) {
                return selectResult.getUid();
            } else return null;
        } catch (Exception e) {
            new ConsolePrints().printSuccessLog(e);
            return null;
        }
    }

    public boolean setUserAiAuthorization(Long uid, boolean status) {
        try {
            ClientUserModel clientUserModel = new ClientUserModel();
            clientUserModel.setUid(uid);
            clientUserModel.setAiAuth(status ? 1 : 0);
            return clientUserDao.updateById(clientUserModel) > 0;
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return false;
        }
    }

    public Boolean getUserAdminStatusByUid(Long uid) {
        try {
            ClientUserModel selectResult = clientUserDao.selectOne(new QueryWrapper<ClientUserModel>().eq("uid", uid));
            if (selectResult != null) {
                return selectResult.getIsAdmin() == 1;
            } else return false;
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return false;
        }
    }

    public Map<String, Object> loginUser(String username, String password, String address) {
        if (username.isEmpty() || password.isEmpty()) return handleResult(408, null, "账号或密码不能为空");
        ServerUserModel selectResult = server_userDao.selectOne(new QueryWrapper<ServerUserModel>().eq("username", username));
        if (selectResult == null || !selectResult.getPassword().equals(utilityService.encodeStringToMD5(password))) {
            return handleResult(409, null, "账号或密码错误");
        } else {
            String token = tokenService.generateUserToken(selectResult.getUid(), username, address);
            new ConsolePrints().printInfoLogV2("User Login " + address + " " + selectResult.getUid() + " " + token);
            return handleResult(200, Map.of("token", token, "userinfo", clientUserDao.selectOne(new QueryWrapper<ClientUserModel>().eq("username", username))), "登录成功");
        }
    }

    public Map<String, Object> registerUser(String username, String password, String address) {
        if (isValidUsername(username) || isValidPassword(password)) return handleResult(400, null, "账号或密码格式不合规");
        if (server_userDao.selectOne(new QueryWrapper<ServerUserModel>().eq("username", username)) != null) {
            return handleResult(409, null, "用户名已存在");
        } else {
            try {
                ServerUserModel serverUserModel = new ServerUserModel();
                long uid = generateUniqueUid();
                Map<String, Object> result = new ClientUserManage().registerUser(serverUserModel, uid, username, utilityService.encodeStringToMD5(password), address);

                @SuppressWarnings("unchecked")
                Map<String, Object> userinfo = (Map<String, Object>) result.get("userinfo");
                result.put("token", tokenService.generateUserToken(Long.parseLong(userinfo.get("uid").toString()), username, address));
                if (server_userDao.insert(serverUserModel) > 0) {
                    return handleResult(200, result, "注册成功");
                } else return handleResult(407, null, "注册失败");
            } catch (Exception e) {
                new ConsolePrints().printErrorLog(e);
                return handleResult(500, null, "服务端异常");
            }
        }
    }

    public Map<String, Object> editForNick(long uid, String nick) {
        if (nick.length() > 10 || nick.length() < 2) return handleResult(400, null, "昵称长度不合规");
        try {
            ServerUserModel serverUserModel = new ServerUserModel();
            Map<String, Object> result = new ClientUserManage().updateNick(serverUserModel, uid, nick);
            if (server_userDao.updateById(serverUserModel) > 0) {
                return handleResult(200, result, "修改昵称成功");
            } else return handleResult(400, result, "修改昵称失败");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResult(500, null, "服务端异常");
        }
    }

    public Map<String, Object> editForAvatar(long uid, String path) {
        try {
            ServerUserModel serverUserModel = new ServerUserModel();
            Map<String, Object> result = new ClientUserManage().updateAvatar(serverUserModel, uid, path);
            if (server_userDao.updateById(serverUserModel) > 0) {
                return handleResult(200, result, "修改头像成功");
            } else return handleResult(400, result, "修改头像失败");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResult(500, null, "服务端异常");
        }
    }

    public Map<String, Object> editForUserName(long uid, String username) {
        if (isValidUsername(username)) return handleResult(400, null, "账号格式不合规");
        try {
            ServerUserModel serverUserModel = new ServerUserModel();
            Map<String, Object> result = new ClientUserManage().updateUserName(serverUserModel, uid, username);
            if (server_userDao.updateById(serverUserModel) > 0) {
                return handleResult(200, result, "修改用户名成功");
            } else return handleResult(400, result, "修改用户名失败");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResult(500, null, "服务端异常");
        }
    }

    public Map<String, Object> editForPassword(long uid, String password) {
        if (isValidPassword(password)) return handleResult(400, null, "密码格式不合规");
        try {
            ServerUserModel serverUserModel = new ServerUserModel();
            new ClientUserManage().updatePassword(serverUserModel, uid, utilityService.encodeStringToMD5(password));
            if (server_userDao.updateById(serverUserModel) > 0) {
                return handleResult(200, null, "修改密码成功");
            } else return handleResult(400, null, "修改密码失败");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResult(500, null, "服务端异常");
        }
    }

    private long generateUniqueUid() {
        IDGenerator idGenerator = new IDGenerator();
        long uid;
        do {
            uid = idGenerator.generateRandomId(8);
        } while (server_userDao.selectOne(new QueryWrapper<ServerUserModel>().eq("uid", uid)) != null);
        return uid;
    }

    private Map<String, Object> handleResult(int code, Object data, String message) {
        return new HandleResults().handleResultByCode(code, data, message);
    }

    private boolean isValidUsername(String username) {
        return !username.matches(USERNAME_PATTERN) || username.length() < 5 || username.length() > 20;
    }

    private boolean isValidPassword(String password) {
        return !password.matches(PASSWORD_PATTERN) || password.length() < 5 || password.length() > 50;
    }
}

package com.server.handsock.sockets.service;

import com.server.handsock.console.MD5Encoder;
import org.springframework.stereotype.Service;
import com.server.handsock.generat.IDGenerator;
import com.server.handsock.admin.dao.OR_UserDao;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.service.TokenService;
import com.corundumstudio.socketio.SocketIOClient;
import com.server.handsock.admin.mod.OR_UserModel;
import com.server.handsock.sockets.mod.OP_UserModel;
import com.server.handsock.sockets.dao.OP_UserDao;
import com.server.handsock.sockets.man.OP_UserManage;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.Map;

@Service
public class OP_UserService {

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]+$";
    private static final String PASSWORD_PATTERN = "^[a-zA-Z0-9]+$";

    private final OP_UserDao op_userDao;
    private final OR_UserDao or_userDao;
    private final TokenService tokenService;

    @Autowired
    public OP_UserService(OR_UserDao or_userDao, OP_UserDao ODUerDao, TokenService tokenService) {
        this.or_userDao = or_userDao;
        this.op_userDao = ODUerDao;
        this.tokenService = tokenService;
    }

    public OP_UserModel queryUserInfo(long uid) {
        return op_userDao.selectById(uid);
    }

    public Boolean getUserAdminStatus(SocketIOClient client) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> authToken = (Map<String, Object>) client.getHandshakeData().getAuthToken();

            long uid = Long.parseLong(authToken.get("uid").toString());
            OP_UserModel selectResult = op_userDao.selectOne(new QueryWrapper<OP_UserModel>().eq("uid", uid));
            if (selectResult != null) {
                return selectResult.getIsAdmin() == 1;
            } else return false;
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return false;
        }
    }

    public Map<String, Object> queryAllUser() {
        try {
            return handleResult(200, op_userDao.selectList(null), "获取成功");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResult(500, null, "服务端异常");
        }
    }

    public Boolean checkUserLogin(long uid) {
        try {
            return or_userDao.selectOne(new QueryWrapper<OR_UserModel>().eq("uid", uid)) != null;
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return false;
        }
    }

    public Boolean getUserAdminStatusByUid(Long uid) {
        try {
            OP_UserModel selectResult = op_userDao.selectOne(new QueryWrapper<OP_UserModel>().eq("uid", uid));
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
        OR_UserModel selectResult = or_userDao.selectOne(new QueryWrapper<OR_UserModel>().eq("username", username));
        if (selectResult == null || !selectResult.getPassword().equals(new MD5Encoder().toMD5(password))) {
            return handleResult(409, null, "账号或密码错误");
        } else {
            String token = tokenService.generateUserToken(selectResult.getUid(), username, address);
            new ConsolePrints().printInfoLogV2("User Login " + address + " " + selectResult.getUid() + " " + token);
            return handleResult(200, Map.of("token", token, "userinfo", op_userDao.selectOne(new QueryWrapper<OP_UserModel>().eq("username", username))), "登录成功");
        }
    }

    public Map<String, Object> registerUser(String username, String password, String address) {
        if (isValidUsername(username) || isValidPassword(password)) return handleResult(400, null, "账号或密码格式不合规");
        if (or_userDao.selectOne(new QueryWrapper<OR_UserModel>().eq("username", username)) != null) {
            return handleResult(409, null, "用户名已存在");
        } else {
            try {
                OR_UserModel or_userModel = new OR_UserModel();
                long uid = generateUniqueUid();
                Map<String, Object> result = new OP_UserManage().registerUser(or_userModel, uid, username, new MD5Encoder().toMD5(password), address);

                @SuppressWarnings("unchecked")
                Map<String, Object> userinfo = (Map<String, Object>) result.get("userinfo");
                result.put("token", tokenService.generateUserToken(Long.parseLong(userinfo.get("uid").toString()), username, address));
                if (or_userDao.insert(or_userModel) > 0) {
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
            OR_UserModel or_userModel = new OR_UserModel();
            Map<String, Object> result = new OP_UserManage().updateNick(or_userModel, uid, nick);
            if (or_userDao.updateById(or_userModel) > 0) {
                return handleResult(200, result, "修改昵称成功");
            } else return handleResult(400, result, "修改昵称失败");
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResult(500, null, "服务端异常");
        }
    }

    public Map<String, Object> editForAvatar(long uid, String path) {
        try {
            OR_UserModel or_userModel = new OR_UserModel();
            Map<String, Object> result = new OP_UserManage().updateAvatar(or_userModel, uid, path);
            if (or_userDao.updateById(or_userModel) > 0) {
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
            OR_UserModel or_userModel = new OR_UserModel();
            Map<String, Object> result = new OP_UserManage().updateUserName(or_userModel, uid, username);
            if (or_userDao.updateById(or_userModel) > 0) {
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
            OR_UserModel or_userModel = new OR_UserModel();
            new OP_UserManage().updatePassword(or_userModel, uid, new MD5Encoder().toMD5(password));
            if (or_userDao.updateById(or_userModel) > 0) {
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
        } while (or_userDao.selectOne(new QueryWrapper<OR_UserModel>().eq("uid", uid)) != null);
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

package com.server.handsock.sockets.eventer;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.ExternalFetcher;
import com.server.handsock.services.ClientService;
import com.server.handsock.clients.mod.ClientUserModel;
import com.server.handsock.clients.service.ClientUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@Service
public class RobotsEvent {

    private final ClientUserService clientUserService;
    private final ExternalFetcher externalFetcher = new ExternalFetcher();
    private final ConsolePrints consolePrints = new ConsolePrints();

    @Autowired
    public RobotsEvent(ClientUserService clientUserService) {
        this.clientUserService = clientUserService;
    }

    private final ArrayList<String> commandList = new ArrayList<>(){{
        add("handsock help"); // 使用帮助
        add("handsock userinfo"); // 用户信息
        add("handsock hitokoto"); // 获取一言
        add("handsock apply-ai"); // 开启权限
        add("handsock weibo-hot"); // 微博热搜
        add("handsock bilibili-hot"); // Bilibili热搜
    }};

    /**
     * 处理机器人发送
     * @return String
     * @apiNote 机器人只能发送字符串消息，可以包含HTML标签，不能包含JS脚本（为了前端安全着想）
     */
    public String handleRobotCommand(SocketIOClient client, SocketIOServer server, String command, Map<String, Object> service, Map<String, Object> clientServiceList ) {
        ClientService clientService = new ClientService(service, clientServiceList);

        Long uid = clientService.getRemoteUID(client); // 获取用户ID
        boolean isAdmin = clientService.getIsAdmin(client); // 判断用户是否是管理员

        try {
            if (Objects.equals(command, commandList.get(0))) {
                return
                        "=======handsock=======<br/>"
                        + "userinfo：用户信息<br/>"
                        + "hitokoto：获取一言<br/>"
                        + "weibo-hot：微博热搜<br/>"
                        + "bilibili-hot：Bilibili热搜<br/>"
                        + "=====================";
            }

            if (Objects.equals(command, commandList.get(1))) {
                try {
                    ClientUserModel clientUserModel = clientUserService.queryUserInfo(uid);
                    return
                            "账号：" + clientUserModel.getUsername() + "<br/>" +
                            "昵称：" + clientUserModel.getNick() + "<br/>" +
                            "注册：" + clientUserModel.getRegTime();
                } catch (Exception e) {
                    consolePrints.printErrorLog(e);
                    return "操作失败，请查看系统日志";
                }
            }

            if (Objects.equals(command, commandList.get(2))) return externalFetcher.getHitokoto();
            if (Objects.equals(command, commandList.get(3))) {
                if (!clientService.hasAiAuthorization(client)) {
                    return clientUserService.setUserAiAuthorization(uid, true) ? "已为您开启AI能力" : "操作失败，请查看系统日志";
                } else return "你已拥有AI能力，无需重复开启";
            }

            if (Objects.equals(command, commandList.get(4))) return externalFetcher.getWeiboHotSearch();
            if (Objects.equals(command, commandList.get(5))) return externalFetcher.getBilibiliHotSearch();
            return null;
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return null;
        }
    }
}

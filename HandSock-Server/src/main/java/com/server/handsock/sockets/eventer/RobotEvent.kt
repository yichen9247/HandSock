package com.server.handsock.sockets.eventer

import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.clients.service.ClientUserService
import com.server.handsock.services.ClientService
import com.server.handsock.utils.ConsoleUtils
import com.server.handsock.utils.ExternalFetcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RobotEvent @Autowired constructor(
    private val clientUserService: ClientUserService,
    private val clientService: ClientService
) {
    private val commandList: ArrayList<String?> = object : ArrayList<String?>() {
        init {
            add("handsock help") // 使用帮助
            add("handsock userinfo") // 用户信息
            add("handsock hitokoto") // 获取一言
            add("handsock apply-ai") // 开启权限
            add("handsock weibo-hot") // 微博热搜
            add("handsock bilibili-hot") // Bilibili热搜
        }
    }

    /**
     * 处理机器人发送
     *
     * @return String
     * @apiNote 机器人只能发送字符串消息，可以包含HTML标签，不能包含JS脚本（为了前端安全着想）
     */
    fun handleRobotCommand(client: SocketIOClient?, command: String): String? {
        val uid = clientService.getRemoteUID(client!!) // 获取用户ID
        /*val isAdmin = clientService.getIsAdmin(client) // 判断用户是否是管理员*/

        try {
            if (command == commandList[0]) {
                return ("=======handsock=======<br/>"
                        + "userinfo：用户信息<br/>"
                        + "hitokoto：获取一言<br/>"
                        + "weibo-hot：微博热搜<br/>"
                        + "bilibili-hot：Bilibili热搜<br/>"
                        + "=====================")
            }

            if (command == commandList[1]) {
                try {
                    val clientUserModel = clientUserService.queryUserInfo(uid)
                    return "账号：${clientUserModel.username}<br/>" +
                            "昵称：${clientUserModel.nick}<br/>" +
                            "注册：${clientUserModel.regTime}"
                } catch (e: Exception) {
                    ConsoleUtils.printErrorLog(e)
                    return "操作失败，请查看系统日志"
                }
            }

            if (command == commandList[2]) return ExternalFetcher.hitokoto
            if (command == commandList[3]) {
                return if (!clientService.hasAiAuthorization(client)) {
                    if (clientUserService.setUserAiAuthorization(uid, true)) "已为您开启AI能力" else "操作失败，请查看系统日志"
                } else "你已拥有AI能力，无需重复开启"
            }

            if (command == commandList[4]) return ExternalFetcher.weiboHotSearch
            if (command == commandList[5]) return ExternalFetcher.bilibiliHotSearch
            return null
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
            return null
        }
    }
}

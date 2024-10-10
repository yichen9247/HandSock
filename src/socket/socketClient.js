/* eslint-disable no-undef */

import utils from "@/scripts/utils"
import { io } from "socket.io-client"
import config from "@/scripts/config"

const openRegisterDialog = async () => {
    const onlineChatStore = utils.useOnlineChatStore();
    ElMessageBox.prompt('请输入你在此聊天室中的昵称', '创建身份', { confirmButtonText: '创建', cancelButtonText: '取消' }).then(({ value }) => {
        if (value === null) {
            openRegisterDialog();
            utils.showToasts('warning', '请输入正确的聊天室昵称！');
        } else {
            onlineChatStore.setLogind(true);
            onlineChatStore.setUserName(value);
            const userid = utils.generUserId();
            onlineChatStore.setUserId(userid);
            localStorage.setItem('userid', userid);
            localStorage.setItem('username', value);
            onlineChatStore.socketIo.emit('[JOIN：103]', { name: value, userid: userid, userqq: onlineChatStore.userqq, channel: onlineChatStore.chatChannel, clientCorsPassword: config.clientCorsPassword });
        }
    }).catch(() => utils.showToasts('info', '正在以游客的身份进行浏览！'));
}

const scrollMessage = async (messageObject) => {
    const onlineChatStore = utils.useOnlineChatStore();
    if (messageObject.channel === onlineChatStore.chatChannel) {
        setTimeout(() => document.querySelector(".content-main").scrollTo({ top: document.querySelector(".content-main").scrollHeight, behavior: 'smooth' }), 100);
    }
}

const startSocketIo = async () => {
    const onlineChatStore = utils.useOnlineChatStore();
    onlineChatStore.setSocketIo(io(config.serverIOAdress, {
        auth: (token) => {
            token({ token: localStorage.getItem('serverToken') });
        }
    }));

    onlineChatStore.socketIo.on('connect', () => {
        setTimeout(() => onlineChatStore.setConnection(true), 800);
        if (localStorage.getItem('userid') !== null && localStorage.getItem('username') !== null) {
          onlineChatStore.setLogind(true);
          onlineChatStore.setUserName(localStorage.getItem('username'));
          onlineChatStore.setUserId(Number(localStorage.getItem('userid')));
        } else openRegisterDialog(onlineChatStore.socket);
        if (localStorage.getItem('userqq') !== null) onlineChatStore.setUserQQ(localStorage.getItem('userqq'));
    });

    onlineChatStore.socketIo.on('disconnect', () => {
        onlineChatStore.setOnlineUsers(0);
        onlineChatStore.setConnection(false);
        utils.showErrorToasts('error', '服务器崩了', '与通信服务器的连接已断开！');
    });
    onlineChatStore.socketIo.on('[WARNING]', (messageObject) => utils.showToasts('error', messageObject.message));

    /**
     * TOKEN：从服务端获取TOKEN
     */
    onlineChatStore.socketIo.on('[TOKEN]', (messageObject) => {
        localStorage.setItem('serverToken', messageObject.token);
    });

    /**
     * 100：统计当前在线用户
     */
    onlineChatStore.socketIo.on('[ONLINE：100]', (messageObject) => {
        onlineChatStore.setOnlineUsers(messageObject.data);
    });

    /**
     * 101：触发用户加入事件
     */
    onlineChatStore.socketIo.on('[JOIN：101]', (messageObject) => {
        onlineChatStore.messageList.push(messageObject) & scrollMessage(messageObject);
    });

    /**
     * 102：服务端发送聊天记录
     */
    onlineChatStore.socketIo.on('[HISTORY：102]', (messageObject) => {
        messageObject.data.forEach(element => onlineChatStore.setMessageList(JSON.parse(element))) & scrollMessage(messageObject);
    });

    /**
     * 103：触发用户拍拍事件
     */
    onlineChatStore.socketIo.on('[CLAP：103]', (messageObject) => {
        onlineChatStore.messageList.push(messageObject) & scrollMessage(messageObject);
    });

    /**
     * 201：触发用户消息事件
     */
    onlineChatStore.socketIo.on('[CHAT：201]', (messageObject) => {
        onlineChatStore.messageList.push(messageObject) & scrollMessage(messageObject);
    });

    /**
     * 203：触发机器回复事件
     */
    onlineChatStore.socketIo.on('[ROBOT：203]', (messageObject) => {
        onlineChatStore.messageList.push(messageObject) & scrollMessage(messageObject);
    });

    /**
     * 400：文本检测返回结果
     */
    onlineChatStore.socketIo.on('[Valid：400]', (messageObject) => {
        utils.showToasts('error', messageObject.message);
    });

    /**
     * 403：密钥权限验证失败
     */
    onlineChatStore.socketIo.on('[CROS：403]', (messageObject) => {
        utils.showToasts('error', messageObject.message);
    });

    /**
     * 500：服务端的异常情况
     */
    onlineChatStore.socketIo.on('[SERVER：500]', (messageObject) => {
        ElMessage({ message: messageObject.message, type: 'warning' }) & scrollMessage(messageObject);
    });
}

const sendJoinMessage = async (channel) => {
    const onlineChatStore = utils.useOnlineChatStore();
    onlineChatStore.logind ? onlineChatStore.socketIo.emit('[JOIN：101]', { name: onlineChatStore.username, userid: onlineChatStore.userid, userqq: onlineChatStore.userqq, channel: channel, clientCorsPassword: config.clientCorsPassword }) : openRegisterDialog();
}

let interval = 0;
const sendChatMessage = async (code, message) => {
    const onlineChatStore = utils.useOnlineChatStore();
    if (!onlineChatStore.logind) return openRegisterDialog();
    if (interval === 0) {
        interval = 3;
        let intervalTimer = setInterval(() => {
            interval--;
            if (interval === 0) clearInterval(intervalTimer);
        }, 1000);
        if (code === 103) {
            onlineChatStore.socketIo.emit('[CHAT：103]', { name: onlineChatStore.username, userid: onlineChatStore.userid, message: message, channel: onlineChatStore.chatChannel, userqq: onlineChatStore.userqq, chatCode: 1, clientCorsPassword: config.clientCorsPassword });
        } else code ? onlineChatStore.socketIo.emit('[CHAT：201]', { name: onlineChatStore.username, userid: onlineChatStore.userid, message: message, channel: onlineChatStore.chatChannel, userqq: onlineChatStore.userqq, chatCode: 1 }) : onlineChatStore.socketIo.emit('[CHAT：201]', { name: onlineChatStore.username, userid: onlineChatStore.userid, message: message, channel: onlineChatStore.chatChannel, userqq: onlineChatStore.userqq, chatCode: 0, clientCorsPassword: config.clientCorsPassword });
    } else utils.showErrorToasts('warning', '发送时间过短', '发送消息时间间隔不能低于3秒！');
}

export default { startSocketIo, sendChatMessage, sendJoinMessage }
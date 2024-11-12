/* eslint-disable no-undef */

import Swal from 'sweetalert2'
import utils from "@/scripts/utils"
import { io } from "socket.io-client"
import config from "@/scripts/config"
import { removeLocalStorage } from "@/scripts/storageUtils"

let toggleStatus = false;
export const openRegisterDialog = async () => {
    const applicationStore = utils.useApplicationStore();
    applicationStore.setLoginFormStatus(true);
}

export const scrollMessage = async (messageObject) => {
    if (messageObject.channel === onlineChatStore.chatChannel) {
        setTimeout(() => document.querySelector(".content-main").scrollTo({ top: document.querySelector(".content-main").scrollHeight, behavior: 'smooth' }), 100);
    }
}

export const checkLoginWork = async (func) => {
    const applicationStore = utils.useApplicationStore();
    if (!applicationStore.connection)
        return utils.showErrorToasts('error', '服务器崩了', '与通信服务器的连接已断开！');
    if (!applicationStore.loginStatus)
        return utils.showErrorToasts('warning', '用户未登录', '用户未登录');
    await func();
}

export const startSocketIo = async () => {
    const applicationStore = utils.useApplicationStore();
    applicationStore.setSocketIo(io(config.serverIOAdress, {
        auth: (token) => {
            token({ 
                uid: applicationStore.userInfo.uid, 
                gid: applicationStore.groupInfo.gid,
                token: localStorage.getItem("handsock_token") 
            });
        }
    }));

    applicationStore.socketIo.on('connect', async () => {
        applicationStore.setConnection(true);
        const uid = localStorage.getItem("handsock_uid");
        const token = localStorage.getItem("handsock_token");
        if (token !== null && uid !== null) {
            applicationStore.socketIo.emit("[CLIENT:INIT]", { uid: uid }, async (data) => {
                if (data.code === 200) {
                    applicationStore.setLoginStatus(true);
                    applicationStore.userInfo = data.data.userinfo;
                } else await removeClient();
            });
        }

        Promise.all([
            Swal.close(),
            await initChatGroup(),
            await initChatUserList(),
            await initChatGroupList(),
            await initChatHistoryList(),
        ]);
    });

    applicationStore.socketIo.on('disconnect',async () => {
        applicationStore.setConnection(false);
        applicationStore.onlineUserList.length = 0;
        if (!toggleStatus) await clientClose();
        // utils.showErrorToasts('error', '服务器崩了', '与通信服务器的连接已断开！');
    });
    applicationStore.socketIo.on('[WARNING]', (messageObject) => utils.showToasts('error', messageObject.message));

    applicationStore.socketIo.on('[TOKENS]',async (messageObject) => {
        const applicationStore = utils.useApplicationStore();
        applicationStore.setServerUUID(messageObject.data)
    });

    applicationStore.socketIo.on('[RE:USER:ALL]',async () => {
        await initChatUserList();
    });

    applicationStore.socketIo.on('[RE:USER:NICK]',async (res) => {
        applicationStore.userList.find(item => item.uid === res.data.uid).nick = res.data.nick;
    });

    applicationStore.socketIo.on('[RE:USER:AVATAR]',async (res) => {
        applicationStore.userList.find(item => item.uid === res.data.uid).avatar = res.data.avatar;
    });

    applicationStore.socketIo.on('[RE:USER:USERNAME]',async (res) => {
        applicationStore.userList.find(item => item.uid === res.data.uid).username = res.data.username;
    });

    applicationStore.socketIo.on('[RE:FORCE:LOAD]',async () => {
        if (applicationStore.loginStatus && applicationStore.userList.length > 0 && applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid).isAdmin) return;
        Swal.fire({
            timer: 5000,
            title: "收到强制刷新命令",
            html: "页面将在 <b>5</b> 秒后刷新",
            timerProgressBar: true,
            didOpen: () => {
                let seconds = 5;
                Swal.showLoading();
                const timerElement = Swal.getPopup().querySelector("b");
                timerElement.textContent = seconds;
                const timerInterval = setInterval(() => {
                    seconds--;
                    timerElement.textContent = seconds;
                    if (seconds <= 0) {
                        clearInterval(timerInterval);
                        Swal.close();
                        setTimeout(() => location.reload(), 500);
                    }
                }, 1000);
            },
            willClose: () => clearInterval(timerInterval),
            allowEscapeKey: false,allowOutsideClick: false
        });
    });

    applicationStore.socketIo.on('[RE:FORCE:CONNECT]',async () => {
        if (applicationStore.loginStatus && applicationStore.userList.length > 0 && applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid).isAdmin) return;
        await toggleConnectStatus();
    });

    applicationStore.socketIo.on('[RE:HISTORY:CLEAR]',async () => {
        applicationStore.messageList.length = 0;
        if (applicationStore.loginStatus && applicationStore.userList.length > 0 && applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid).isAdmin) return;
        utils.showErrorToasts('warning', '聊天记录重置', '服务端聊天记录已重置！');
    });

    applicationStore.socketIo.on('[ONLINE]',async (messageObject) => {
        applicationStore.onlineUserList = messageObject.data;
    });

    applicationStore.socketIo.on('[MESSAGE]', (messageObject) => {
        if (applicationStore.groupInfo.gid !== messageObject.gid) return;
        applicationStore.messageList.push(messageObject);
        if (applicationStore.userInfo.uid !== messageObject.uid) {
            if (messageObject.type === 'text' || messageObject.type === 'clap') {
                if (localStorage.getItem('audio-switch') === 'true') utils.playNoticeVoice();
                if (localStorage.getItem('audio-switch') !== 'true' && localStorage.getItem('audio-switch') !== 'false') {
                    utils.playNoticeVoice();
                    localStorage.setItem('audio-switch', true);
                }
            }
        }
        setTimeout(() => document.querySelector(".chat-content-box").scrollTo({ top: document.querySelector(".chat-content-box").scrollHeight, behavior: 'smooth' }), 100);
    });
}

export const clientClose = async () => {
    Swal.fire({
        icon: "error",
        title: "连接已断开",
        allowEscapeKey: false,
        allowOutsideClick: false,
        showConfirmButton: false,
        text: "正在尝试重新连接通信服务器",
    });
}

export const toggleConnectStatus = async (func) => {
    toggleStatus = true;
    const applicationStore = utils.useApplicationStore();
    applicationStore.socketIo.disconnect().connect();
    toggleStatus = false;
    Promise.all(func);
}

export const initChatGroup = async () => {
    const applicationStore = utils.useApplicationStore();
    const channelId = new URLSearchParams(location.search).get("channel");
    applicationStore.socketIo.emit("[SEARCH:GROUP]", {
        gid: (channelId === null || !/^[-+]?\d+$/.test(channelId)) ? 0 : Number(channelId)
    }, (data) => {
        if (data.code === 200) {
            document.title = data.data.name;
            applicationStore.groupInfo = data.data;
            applicationStore.setGroupClosed(false);
        } else {
            document.title = "对应频道未开放";
            applicationStore.setGroupClosed(true);
        }
    });
}

export const initChatUserList = async () => {
    const applicationStore = utils.useApplicationStore();
    applicationStore.socketIo.emit("[SEARCH:USER:ALL]", null, (data) => {
        applicationStore.userList = data.data
    });
}

export const initChatGroupList = async () => {
    const applicationStore = utils.useApplicationStore();
    applicationStore.socketIo.emit("[SEARCH:GROUP:ALL]", null, (data) => {
        applicationStore.chatGroupList = data.data;
    });
}

export const initChatHistoryList = async () => {
    const channelId = new URLSearchParams(location.search).get("channel");
    const applicationStore = utils.useApplicationStore();
    applicationStore.socketIo.emit("[SEARCH:HISTORY:ALL]", {
        gid: (channelId === null || !/^[-+]?\d+$/.test(channelId)) ? 0 : Number(channelId)
    }, (data) => {
        if (data.code !== 200) return ElMessage({ message: data.message, type: 'error' });
        applicationStore.messageList = data.data;
        setTimeout(() => document.querySelector(".chat-content-box").scrollTo({ top: document.querySelector(".chat-content-box").scrollHeight, behavior: 'smooth' }), 100);
    });
}

export const sendJoinMessage = async () => {
    const applicationStore = utils.useApplicationStore();
    applicationStore.socketIo.emit("[SEND:MESSAGE]", {
        type: 'join',
        content: `${applicationStore.userInfo.nick} 加入到了聊天室`
    }, (data) => {
        if (data.code !== 200) return ElMessage({ message: data.message, type: 'error' });
    });
}

export const sendChatMessage = async () => {
    const applicationStore = utils.useApplicationStore();
    await sendSocketEmit("[SEND:MESSAGE]", {
        type: 'text',
        content: applicationStore.chantInput
    }, (data) => {
        if (data.code !== 200) ElMessage({ message: data.message, type: 'error' });
    });
    applicationStore.setChantInput("");
}

export const removeClient = async () => {
    const applicationStore = utils.useApplicationStore();
    applicationStore.resetUserInfo();
    applicationStore.setLoginFormStatus(true);
    utils.showErrorToasts('error', '请重新登录', '请重新登录账号');
    await removeLocalStorage(['handsock_uid','handsock_username','handsock_token']);
}

let interval = 0;
export const sendSocketEmit = async (event, data, callback) => {
    const applicationStore = utils.useApplicationStore();
    if (interval === 0) {
        interval = 3;
        let intervalTimer = setInterval(() => {
            interval--;
            if (interval === 0) clearInterval(intervalTimer);
        }, 1000);
        applicationStore.socketIo.emit(event, data, async (res) => {
            if (res.code === 403) {
                await removeClient();
            } else await callback(res);
        });
    } else utils.showToasts('warning', '操作频率过快');
}

export const toggleChatChannel = async (router, gid) => {
    const applicationStore = utils.useApplicationStore();
    await checkLoginWork(async () => {
        if (applicationStore.groupInfo.gid !== gid) {
            router.push('/?channel=' + gid);
            
            applicationStore.socketIo.auth = (token) => {
                token({ 
                    uid: applicationStore.userInfo.uid, 
                    gid: gid,
                    token: localStorage.getItem("handsock_token") 
                });
            }
            await toggleConnectStatus([
                await initChatGroup(),
                await initChatHistoryList(),
            ]);
        }
    });
}
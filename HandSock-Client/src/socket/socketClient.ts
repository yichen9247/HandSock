import ua2obj from 'ua2obj'
import socket from './socket'
import Swal from 'sweetalert2'
import utils from "@/scripts/utils"
import { io } from "socket.io-client"
import { ElMessage } from 'element-plus'
import { setSearchDialog } from '@/scripts/action'
import { playNoticeVoice } from '@/scripts/audioUtils'
import { messageType, restfulType } from '../../types'
import { removeLocalStorage } from "@/scripts/storageUtils"

let toggleStatus: boolean = false;
const userAgentObj: any = ua2obj();

export const checkLoginWork = async (func: any): Promise<void> => {
    const applicationStore = utils.useApplicationStore();
    if (!applicationStore.connection)
        return utils.showToasts('error', '连接服务器失败');
    if (!applicationStore.loginStatus)
        return utils.showToasts('warning', '用户未登录');
    await func();
}

export const startSocketIo = async (): Promise<void> => {
    const applicationStore = utils.useApplicationStore();
    applicationStore.setSocketIo(io(socket.server.config.serverIP, {
        auth: (token) => {
            token({
                uid: applicationStore.userInfo.uid,
                gid: applicationStore.groupInfo.gid,
                token: localStorage.getItem("handsock_token")
            });
        }
    }));

    applicationStore.socketIo.on('connect', async (): Promise<void> => {
        applicationStore.setConnection(true);
        const uid = localStorage.getItem("handsock_uid");
        const token = localStorage.getItem("handsock_token");
        if (token !== null && uid !== null) {
            applicationStore.socketIo.emit(socket.send.ClientInit, { uid: uid }, async (response: restfulType): Promise<void> => {
                if (response.code === 200) {
                    applicationStore.setLoginStatus(true);
                    applicationStore.userInfo = response.data.userinfo;
                    await resetOnlineUsers(1);
                } else await removeClient();
            });
        }

        Promise.all([
            Swal.close(),
            await initChatGroup(),
            await initChatUserList(),
            await initChatGroupList(),
            await checkClientVersion(),
            await initChatHistoryList(),
        ]);
    });

    applicationStore.socketIo.on('disconnect', async (): Promise<void> => {
        applicationStore.setConnection(false);
        applicationStore.onlineUserList.length = 0;
        if (!toggleStatus) utils.showSwalFire('error', "连接已断开", "正在尝试重新连接通信服务器");
    });
    applicationStore.socketIo.on(socket.rece.Warning, (messageObject: restfulType): Promise<void> => utils.showToasts('error', messageObject.message));

    applicationStore.socketIo.on(socket.rece.Tokens, async (messageObject: restfulType): Promise<void> => {
        const applicationStore = utils.useApplicationStore();
        applicationStore.setServerUUID(messageObject.data)
    });

    applicationStore.socketIo.on(socket.rece.Re.user.ReuserAll, async (): Promise<void> => {
        await initChatUserList();
    });

    applicationStore.socketIo.on(socket.rece.Re.user.ReuserNick, async (response: restfulType): Promise<void> => {
        applicationStore.userList.find(item => item.uid === response.data.uid).nick = response.data.nick;
    });

    applicationStore.socketIo.on(socket.rece.Re.user.ReuserAvatar, async (response: restfulType): Promise<void> => {
        applicationStore.userList.find(item => item.uid === response.data.uid).avatar = response.data.avatar;
    });

    applicationStore.socketIo.on(socket.rece.Re.user.ReuserName, async (response: restfulType): Promise<void> => {
        applicationStore.userList.find(item => item.uid === response.data.uid).username = response.data.username;
    });

    applicationStore.socketIo.on(socket.rece.Re.force.ReforceLoad, async (): Promise<void> => {
        if (applicationStore.loginStatus && applicationStore.userList.length > 0 && applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid).isAdmin) return;
        Swal.fire({
            timer: 5000,
            title: "收到强制刷新命令",
            html: "页面将在 <b style='color: var(--dominColor)'>5</b> 秒后刷新",
            timerProgressBar: true,
            didOpen: (): void => {
                let seconds = 5;
                Swal.showLoading();
                const timerElement: any = Swal.getPopup().querySelector("b");
                timerElement.textContent = seconds;
                const timerInterval = setInterval((): void => {
                    seconds--;
                    timerElement.textContent = seconds;
                    if (seconds <= 0) {
                        clearInterval(timerInterval);
                        Swal.close();
                        setTimeout((): void => location.reload(), 500);
                    }
                }, 1000);
            },
            allowEscapeKey: false, allowOutsideClick: false
        });
    });

    applicationStore.socketIo.on(socket.rece.Re.force.ReforceConnect, async (): Promise<void> => {
        if (applicationStore.loginStatus && applicationStore.userList.length > 0 && applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid).isAdmin) return;
        await toggleConnectStatus(() => {});
    });

    applicationStore.socketIo.on(socket.rece.Re.RehistoryClear, async (): Promise<void> => {
        applicationStore.messageList.length = 0;
        if (applicationStore.loginStatus && applicationStore.userList.length > 0 && applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid).isAdmin) return;
        utils.showToasts('warning', '聊天记录重置');
    });

    applicationStore.socketIo.on(socket.rece.Online, async (messageObject: restfulType): Promise<void> => {
        applicationStore.onlineUserList = messageObject.data;
    });

    applicationStore.socketIo.on(socket.rece.Message, async (messageObject: messageType): Promise<void> => {
        if (applicationStore.groupInfo.gid !== messageObject.gid) return;
        applicationStore.messageList.push(messageObject);
        if (applicationStore.userInfo.uid !== messageObject.uid) {
            if (messageObject.type === 'text' || messageObject.type === 'clap') {
                if (localStorage.getItem('audio-switch') === 'true') await playNoticeVoice();
                if (localStorage.getItem('audio-switch') !== 'true' && localStorage.getItem('audio-switch') !== 'false') {
                    await playNoticeVoice();
                    localStorage.setItem('audio-switch', 'true');
                }
            }
        }
        setTimeout(() => document.querySelector(".chat-content-box").scrollTo({ top: document.querySelector(".chat-content-box").scrollHeight, behavior: 'smooth' }), 100);
    });
}

const checkClientVersion = async (): Promise<void> => {
    const applicationStore = utils.useApplicationStore();
    applicationStore.socketIo.emit(socket.send.ClientCheck, {
        version: socket.application.appVersion
    }, (response: restfulType): void => {
        if (response.code === 200) return;
        utils.showSwalFire('error', "前后端版本不一致", "请将前端和后端更新到同一个版本");
    });
}

const initChatGroup = async (): Promise<void> => {
    const applicationStore = utils.useApplicationStore();
    const channelId = new URLSearchParams(location.search).get("channel");
    applicationStore.socketIo.emit(socket.send.Search.SearchGroup, {
        gid: (channelId === null || !/^[-+]?\d+$/.test(channelId)) ? 0 : Number(channelId)
    }, (response: restfulType): void => {
        if (response.code === 200) {
            document.title = response.data.name;
            applicationStore.groupInfo = response.data;
            applicationStore.setGroupClosed(false);
        } else {
            document.title = "对应频道未开放";
            applicationStore.setGroupClosed(true);
        }
    });
}

const initChatUserList = async (): Promise<void> => {
    const applicationStore = utils.useApplicationStore();
    applicationStore.socketIo.emit(socket.send.Search.SearchUserAll, null, (response: restfulType): void => {
        applicationStore.userList = response.data
    });
}

const initChatGroupList = async (): Promise<void> => {
    const applicationStore = utils.useApplicationStore();
    applicationStore.socketIo.emit(socket.send.Search.SearchGroupAll, null, (response: restfulType) :void => {
        applicationStore.chatGroupList = response.data;
    });
}

const initChatHistoryList = async (): Promise<void> => {
    const channelId = new URLSearchParams(location.search).get("channel");
    const applicationStore = utils.useApplicationStore();
    applicationStore.socketIo.emit(socket.send.Search.SearchHistoryAll, {
        gid: (channelId === null || !/^[-+]?\d+$/.test(channelId)) ? 0 : Number(channelId)
    }, (response: restfulType): any => {
        if (response.code !== 200) return ElMessage({ message: response.message, type: 'error' });
        applicationStore.messageList = response.data;
        setTimeout(() => document.querySelector(".chat-content-box").scrollTo({ top: document.querySelector(".chat-content-box").scrollHeight, behavior: 'smooth' }), 100);
    });
}

export const resetOnlineUsers = async (status: any) => {
    const applicationStore = utils.useApplicationStore();
    applicationStore.socketIo.emit(socket.send.OnlineLogin, { uid: localStorage.getItem("handsock_uid"), platform: userAgentObj.osName + " " + userAgentObj.osVersion, status: status }, () => { });
}

export const toggleConnectStatus = async (func: any) => {
    toggleStatus = true;
    const applicationStore = utils.useApplicationStore();
    applicationStore.socketIo.disconnect().connect();
    toggleStatus = false;
    Promise.all(func);
}

export const openUserLogoutDialog = async (): Promise<void> => {
    const applicationStore = utils.useApplicationStore();
    await checkLoginWork((): void => {
        utils.showConfirms('none', '系统提示', '确定要退出登录吗，你的聊天记录将会丢失？', async (): Promise<void> => {
            await resetOnlineUsers(0);
            applicationStore.setLoginStatus(false);
            applicationStore.userInfo.avatar = applicationStore.defaultAvatar;
            await removeLocalStorage(['handsock_uid', 'handsock_token', 'handsock_username']);
            applicationStore.socketIo.emit("[USER:LOGOUT]", null);
            utils.showToasts('success', '退出登录成功！');
        }, '取消', '退出登录');
    });
}

export const sendChatMessage = async (): Promise<void> => {
    const applicationStore = utils.useApplicationStore();
    await sendSocketEmit(socket.send.SendMessage, {
        type: 'text',
        content: applicationStore.chantInput
    }, (response: restfulType): void => {
        if (response.code !== 200) ElMessage({ message: response.message, type: 'error' });
    });
    applicationStore.setChantInput("");
}

export const removeClient = async (): Promise<void> => {
    const applicationStore = utils.useApplicationStore();
    applicationStore.resetUserInfo();
    utils.showToasts('error', '请重新登录');
    applicationStore.setLoginFormStatus(true);
    await removeLocalStorage(['handsock_uid', 'handsock_username', 'handsock_token']);
}

let interval = 0;
export const sendSocketEmit = async (event: any, data: any, callback: any): Promise<void> => {
    const applicationStore = utils.useApplicationStore();
    if (interval === 0) {
        interval = 3;
        let intervalTimer = setInterval(() => {
            interval--;
            if (interval === 0) clearInterval(intervalTimer);
        }, 1000);
        applicationStore.socketIo.emit(event, data, async (response: restfulType): Promise<void> => {
            if (response.code === 403) {
                await removeClient();
            } else await callback(response);
        });
    } else utils.showToasts('warning', '操作频率过快');
}

export const toggleChatChannel = async (router: any, gid: any): Promise<void> => {
    const applicationStore = utils.useApplicationStore();
    await checkLoginWork(async () => {
        if (applicationStore.groupInfo.gid !== gid) {
            router.push('/?channel=' + gid);
            applicationStore.socketIo.auth = (token: any): any => {
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

export const searchOfChannel = async (router: any, channel: any): Promise<void> => {
    setSearchDialog(false);
    const applicationStore = utils.useApplicationStore();
    if (!/^[-+]?\d+$/.test(channel)) return utils.showToasts('warning', '格式不合规');
    const channelId = Number(channel);
    if (applicationStore.groupInfo.gid === channelId) return utils.showToasts('warning', '重复加入频道');

    await sendSocketEmit(socket.send.Search.SearchGroup, {
        gid: channelId
    }, (response: restfulType): void => {
        if (response.code === 200) {
            const { gid, name } = response.data;
            utils.showConfirms('none', '搜索频道', `查找到频道【${name}】，是否加入？`, async (): Promise<void> => {
                await toggleChatChannel(router, gid);
                utils.showToasts('success', "加入频道成功");
            }, '取消', '进入频道');
        } else utils.showToasts('warning', '未找到该频道');
    });
}
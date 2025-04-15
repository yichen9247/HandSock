import utils from "./utils";
import Swal from "sweetalert2";
import HandUtils from "./HandUtils";
import socket from "../socket/socket";
import { startSocketIo } from "../socket/socketClient";
import { UserAuthType } from "../../types/UserAuthType";
import { useApplicationStore } from "@/stores/applicationStore";
import { aiEventHandleType, groupInfoType, messageType, restfulType, userInfoType } from "../../types";

let toggleStatus: boolean = false;

export default class SocketUtils {
    static onCreateAiMessage = async (response: restfulType<aiEventHandleType>): Promise<void> => {
        const applicationStore = useApplicationStore();
        if (response.code === 200) {
            if (response.data.event === "CREATE-MESSAGE") {
                const applicationStore = utils.useApplicationStore();
                await HandUtils.playNotificationSound(response.data.result);
                applicationStore.aiMessageList.push(response.data.result);
            }
            if (response.data.event === "PUSH-STREAM") {
                if (applicationStore.aiMessageList.find(item => item.sid === response.data.eventId).content === "正在请求中") applicationStore.aiMessageList.find(item => item.sid === response.data.eventId).content = "";
                applicationStore.aiMessageList.find(item => item.sid === response.data.eventId).content += response.data.content;
            }
        } else await utils.showToasts('error', response.message);
    }

    static onSocketIoDisconnect = (): void => {
        const applicationStore = useApplicationStore();
        applicationStore.setConnection(false);
        applicationStore.onlineUserList = [];
        if (!toggleStatus) HandUtils.showDialogFireBySwal({
            icon: "error",
            title: "连接已断开",
            text: "正在尝试重新连接通信服务器"
        });
    }

    static onReHistoryClear = async (): Promise<void> => {
        const applicationStore = useApplicationStore();
        applicationStore.messageList.length = 0;
        await utils.showToasts('warning', '聊天记录重置');
    }

    static onReForceConnect = async (): Promise<void> => {
        if (HandUtils.getUserPermission() === UserAuthType.ADMIN_AUTHENTICATION) return;
        await startSocketIo();
    }

    static onReForceLoad = async (): Promise<void> => {
        if (HandUtils.getUserPermission() === UserAuthType.ADMIN_AUTHENTICATION) return;
        await Swal.fire({
            timer: 5000,
            title: "收到强制刷新命令",
            html: "页面将在 <b style='color: var(--dominColor)'>5</b> 秒后刷新",
            timerProgressBar: true,
            didOpen: () => {
                let seconds = 5;
                Swal.showLoading();
                const timerElement = Swal.getPopup()?.querySelector("b");
                if (!timerElement) return;
                const timerInterval = setInterval(() => {
                    seconds--;
                    timerElement.textContent = seconds.toString();
                    if (seconds <= 0) {
                        clearInterval(timerInterval);
                        Swal.close();
                        setTimeout(() => location.reload(), 500);
                    }
                }, 1000);
            },
            allowEscapeKey: false,
            allowOutsideClick: false,
        });
    }

    static initChatUserList = async (): Promise<void> => {
        const applicationStore = utils.useApplicationStore();
        applicationStore.socketIo.emit(socket.send.Search.All.SearchUserAll, null, (response: restfulType<Array<userInfoType>>): void => {
            applicationStore.userList = response.data;
        });
    }

    static initChatGroupList = async (): Promise<void> => {
        const applicationStore = utils.useApplicationStore();
        if (applicationStore.chatGroupList.length > 0) return;
        applicationStore.socketIo.emit(socket.send.Search.All.SearchGroupAll, null, (response: restfulType<Array<groupInfoType>>) :void => {
            applicationStore.chatGroupList = response.data;
        });
    }

    static initChatGroup = async (): Promise<void> => {
        const applicationStore = utils.useApplicationStore();
        const channelId = new URLSearchParams(location.search).get("channel");
        applicationStore.socketIo.emit(socket.send.Search.SearchGroup, {
            gid: (channelId === null || !/^[-+]?\d+$/.test(channelId)) ? 0 : Number(channelId)
        }, async (response: restfulType<groupInfoType>): Promise<void> => {
            if (response.code === 200) {
                document.title = response.data.name;
                applicationStore.setGroupClosed(false);
                const { gid, aiRole, active } = response.data;
                applicationStore.groupInfo = response.data;
                applicationStore.groupInfo.aiRole = aiRole;
                await SocketUtils.initChatHistoryList(aiRole);
                if (active && !applicationStore.chatGroupList.some(item => item.gid === gid)) applicationStore.chatGroupList.push(response.data);
            } else {
                document.title = "对应频道未开放";
                applicationStore.setGroupClosed(true);
            }
        });
    }

    static initChatHistoryList = async (aiRole: boolean): Promise<void> => {
        if (aiRole) return;
        const applicationStore = utils.useApplicationStore();
        const channelId = new URLSearchParams(location.search).get("channel");
        applicationStore.socketIo.emit(socket.send.Search.All.SearchHistoryAll, {
            gid: (channelId === null || !/^[-+]?\d+$/.test(channelId)) ? 0 : Number(channelId)
        }, (response: restfulType<Array<messageType>>): any => {
            if (response.code !== 200) return utils.showToasts('error', response.message);
            applicationStore.messageList = response.data;
        });
    }
}
import socket from './socket'
import Swal from 'sweetalert2'
import utils from "@/scripts/utils"
import { io } from "socket.io-client"
import HandUtils from '../scripts/HandUtils'
import { messageType, restfulType } from '../../types'

export const startSocketIo = async (): Promise<void> => {
    const applicationStore = utils.useApplicationStore();
    applicationStore.setSocketIo(
		io(socket.server.config.serverIP, {
			auth: (token) => {
				token({
					uid: applicationStore.userInfo.uid,
					gid: applicationStore.groupInfo.gid,
					token: localStorage.getItem("handsock_token"),
				});
			},
		})
	);

	type EventHandler = (messageObject: restfulType) => void | Promise<void>;

	const eventHandlers: Record<string, EventHandler> = {
		connect: async () => {
			applicationStore.setConnection(true);
			const uid = localStorage.getItem("handsock_uid");
			const token = localStorage.getItem("handsock_token");
			if (token && uid) {
				applicationStore.socketIo.emit(socket.send.ClientInit, { uid }, async (response: restfulType) => {
                    if (response.code === 200) {
						applicationStore.setLoginStatus(true);
						applicationStore.userInfo = response.data.userinfo;
						await HandUtils.resetOnlineUsers(1);
					} else await HandUtils.removeClientLoginStatus();
				});
			}

			await Promise.all([
				Swal.close(),
				HandUtils.initChatGroup(),
				HandUtils.initChatUserList(),
				HandUtils.initChatGroupList(),
				HandUtils.checkClientVersion(),
			]);
		},
		[socket.rece.Tokens]: async (messageObject) => {
			applicationStore.setServerUUID(messageObject.data);
		},
		[socket.rece.Re.user.ReuserNick]: (response) => {
			const user = applicationStore.userList.find(
				(item) => item.uid === response.data.uid
			);
			if (user) user.nick = response.data.nick;
		},
		[socket.rece.Re.user.ReuserAvatar]: (response) => {
			const user = applicationStore.userList.find(
				(item) => item.uid === response.data.uid
			);
			if (user) user.avatar = response.data.avatar;
		},
		[socket.rece.Re.user.ReuserName]: (response) => {
			const user = applicationStore.userList.find(
				(item) => item.uid === response.data.uid
			);
			if (user) user.username = response.data.username;
		},
        disconnect: () => HandUtils.onSocketIoDisconnect(),
        [socket.rece.Re.user.ReuserAll]: () => HandUtils.initChatUserList(),
		[socket.rece.Re.force.ReforceLoad]: () => HandUtils.onReceRefreshConnect(),
        [socket.rece.Warning]: (messageObject) => utils.showToasts("error", messageObject.message)
	};

	for (const [event, handler] of Object.entries(eventHandlers)) {
		applicationStore.socketIo.on(event, handler);
	}

	applicationStore.socketIo.on(
		socket.rece.Re.force.ReforceConnect,
		async (): Promise<void> => {
			if (
				applicationStore.loginStatus &&
				applicationStore.userList.length > 0 &&
				applicationStore.userList.find(
					(item) => item.uid === applicationStore.userInfo.uid
				).isAdmin
			) return;
			await HandUtils.toggleConnectStatus(() => {});
		}
	);

    applicationStore.socketIo.on(socket.rece.Re.RehistoryClear, async (): Promise<void> => {
        applicationStore.messageList.length = 0;
        if (applicationStore.loginStatus && applicationStore.userList.length > 0 && applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid).isAdmin) return;
        utils.showToasts('warning', '聊天记录重置');
    });

    applicationStore.socketIo.on(socket.rece.Online, async (messageObject: restfulType): Promise<void> => {
        applicationStore.onlineUserList = messageObject.data;
    });

    applicationStore.socketIo.on(socket.rece.Message, async (messageObject: messageType): Promise<void> => {
        applicationStore.messageList.push(messageObject);
        HandUtils.playNotificationSound(messageObject);
        setTimeout(() => document.querySelector(".chat-content-box").scrollTo({ top: document.querySelector(".chat-content-box").scrollHeight, behavior: 'smooth' }), 100);
    });

    applicationStore.socketIo.on(socket.rece.AI.CreateMessage, async (response: restfulType): Promise<void> => {
        if (response.code === 200) {
            if (response.data.event === "CREATE-MESSAGE") {
                const applicationStore = utils.useApplicationStore();
                HandUtils.playNotificationSound(response.data.result);
                applicationStore.aiMessageList.push(response.data.result);
            }
            if (response.data.event === "PUSH-STREAM") {
                if (applicationStore.aiMessageList.find(item => item.sid === response.data.eventId).content === "正在请求中") applicationStore.aiMessageList.find(item => item.sid === response.data.eventId).content = "";
                applicationStore.aiMessageList.find(item => item.sid === response.data.eventId).content += response.data.content;
            }
            setTimeout(() => document.querySelector(".chat-content-box").scrollTo({ top: document.querySelector(".chat-content-box").scrollHeight, behavior: 'smooth' }), 100);
        } else utils.showToasts('error', response.message);
    });
}
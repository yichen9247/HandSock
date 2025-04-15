import socket from './socket'
import Swal from 'sweetalert2'
import utils from "@/scripts/utils"
import { io } from "socket.io-client"
import HandUtils from '../scripts/HandUtils'
import SocketUtils from "../scripts/SocketUtils";
import { aiEventHandleType, messageType, restfulType, userInfoType } from '../../types'

let pingInterval = null;
type EventHandler = (messageObject: restfulType<any>) => void | Promise<void>;

export const startSocketIo = async (): Promise<void> => {
    const applicationStore = utils.useApplicationStore();

	if (pingInterval) clearInterval(pingInterval);
	if (applicationStore.socketIo) applicationStore.socketIo.removeAllListeners();

	const channelId = new URLSearchParams(location.search).get("channel");
	const currentId = (channelId === null || !/^[-+]?\d+$/.test(channelId)) ? 0 : Number(channelId);

    applicationStore.setSocketIo(
		io(socket.server.config.serverIP, {
			auth: (token) => {
				token({
					gid: currentId,
					uid: applicationStore.userInfo.uid,
					token: localStorage.getItem("handsock_token"),
				});
			},
		})
	);

	const eventHandlers: Record<string, EventHandler> = {
		connect: async () => {
			applicationStore.setConnection(true);
			const uid = localStorage.getItem("handsock_uid");
			const token = localStorage.getItem("handsock_token");
			if (token && uid) {
				applicationStore.socketIo.emit(socket.send.ClientInit, { uid }, async (response: restfulType<userInfoType>) => {
                    if (response.code === 200) {
						applicationStore.setLoginStatus(true);
						applicationStore.userInfo = response.data;
						await HandUtils.resetOnlineUsers(1);
					} else await HandUtils.removeClientLoginStatus();
				});
			}

			Swal.close()
			await Promise.all([
				SocketUtils.initChatUserList(),
				SocketUtils.initChatGroupList(),
				SocketUtils.initChatGroup(),
				HandUtils.checkClientVersion(),
			]);
			pingInterval = setInterval(() => {
				applicationStore.socketIo.emit(socket.send.ClientPing, { type: 'Client Ping' }, async (response: restfulType<any>) => {
					if (response.code !== 200) await utils.showToasts('error', '心跳异常');
				});
			}, 3000);
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
        disconnect: () => SocketUtils.onSocketIoDisconnect(),
        [socket.rece.Re.user.ReuserAll]: () => SocketUtils.initChatUserList(),
		[socket.rece.Re.RehistoryClear]: () => SocketUtils.onReHistoryClear(),
		[socket.rece.Re.force.ReforceLoad]: () => SocketUtils.onReForceLoad(),
		[socket.rece.Re.force.ReforceConnect]: () => SocketUtils.onReForceConnect(),
		[socket.rece.Online]: (messageObject) => applicationStore.onlineUserList = messageObject.data,
        [socket.rece.Warning]: (messageObject) => utils.showToasts("error", messageObject.message),
		[socket.rece.AI.CreateMessage]: (response: restfulType<aiEventHandleType>) => SocketUtils.onCreateAiMessage(response),
	}

	for (const [event, handler] of Object.entries(eventHandlers)) {
		applicationStore.socketIo.on(event, handler);
	}

    applicationStore.socketIo.on(socket.rece.Message, async (message: messageType): Promise<void> => {
		const { gid, uid, content, type } = message;
		const { chatList, groupInfo, userInfo } = applicationStore;
		const isCurrentGroup = gid === groupInfo.gid;
	
		const updateOrCreateChatItem = () => {
			const chatItem = chatList.find(item => item.gid === gid);
			if (chatItem) {
				Object.assign(chatItem, { uid, type, content });
				return chatItem;
			}
			const newItem = { uid, gid, type, content, num: 0};
			chatList.push(newItem);
			return newItem;
		};
	
		const chatItem = updateOrCreateChatItem();
		if (!isCurrentGroup) {
			chatItem.num++;
			return;
		}
	
		applicationStore.messageList.push(message);
		if (userInfo.uid !== uid) await HandUtils.playNotificationSound(message);
		setTimeout(() => document.querySelector(".chat-content-box").scrollTo({ top: document.querySelector(".chat-content-box").scrollHeight, behavior: 'smooth' }), 100);
	});
}
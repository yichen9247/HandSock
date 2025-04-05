import ua2obj from "ua2obj";
import Swal from "sweetalert2";
import utils from "@/scripts/utils";
import socket from "../socket/socket";
import { setSearchDialog } from "@/scripts/action";
import { playNoticeVoice } from "@/scripts/audioUtils";
import { saveLocalStorage } from '@/scripts/storageUtils';
import { removeLocalStorage } from "@/scripts/storageUtils";
import { useApplicationStore } from "@/stores/applicationStore";
import { messageType, restfulType, userInfoType } from "../../types";

let interval = 0, toggleStatus: boolean = false;

class HandUtils {
	static getClientPlatform(): String {
		const userAgentObj = ua2obj();
        return `${userAgentObj.osName} ${userAgentObj.osVersion}`;
	}

	static getClientToken = (): string => {
		return localStorage.getItem("handsock_token");
	}

	static getUserTypeByInfo = (userinfo: userInfoType): string => {
		if (userinfo.isAdmin === 1) return "admin";
		else
		if (userinfo.isRobot === 1) return "robot";
		else return "normal";
	}

	static previewImageBySwal = ({ src, html }): void => {
		Swal.fire({
			imageWidth: "90%",
			showConfirmButton: false,
			html: html, imageUrl: src,
		});
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

	static onReceRefreshConnect = async (): Promise<void> => {
		const applicationStore = useApplicationStore();
		const isAdmin = applicationStore.loginStatus && applicationStore.userList.length > 0 && 
			applicationStore.userList.find(
				(item) => item.uid === applicationStore.userInfo.uid
			).isAdmin == 1;
		if (isAdmin) return;
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

	static getMessageType = ({ type, content }): string => {
		return type === 'text' ? content : type === 'image' ?'图片消息' : type === 'file' ? '文件消息' : '未知消息';
	}

	static getUserAvatarByPath = (path: string): string => {
		return `${socket.server.config.serverUrl}${socket.server.downloadAvatar}${path}`;
	}

	static getFileSizeByUnit = ({ fileSize, mode }): number => {
		const fileSizeKB = fileSize / 1024.0;
		if (mode === 'KB') {
			return parseFloat(fileSizeKB.toFixed(2));
		} else if (mode === 'MB') {
			return parseFloat((fileSizeKB / 1024.0).toFixed(2));
		} else return null;
	}

	static getUserInfoByUid = (uid: string): userInfoType => {
		const applicationStore = useApplicationStore();
		return applicationStore.userList.find(item => item.uid === uid);
	}

	static getMessageInfoBySid = (sid: string): messageType => {
		const applicationStore = useApplicationStore();
		return applicationStore.messageList.find(item => item.sid === sid);
	}

	static checkClientLoginStatus = async (call: any) => {
		const applicationStore = utils.useApplicationStore();
		if (!applicationStore.connection)
			return utils.showToasts('error', '连接服务器失败');
		if (!applicationStore.loginStatus) {
			const onelDialogStore = utils.useOnelDialogStore();
			return onelDialogStore.setUserLoginCenter(true);
		}
		await call();
	}

	static showDialogFireBySwal = ({ icon, title, text, allowClose = false }): void => {
		Swal.fire({
			showConfirmButton: false,
			icon: icon, text: text, title: title,
			allowEscapeKey: allowClose, allowOutsideClick: allowClose
		});
	}

	static removeClientLoginStatus = async (): Promise<void> => {
		const onelDialogStore = utils.useOnelDialogStore();
		const applicationStore = utils.useApplicationStore();
		applicationStore.resetUserInfo();
		utils.showToasts('error', '请重新登录');
		onelDialogStore.setUserLoginCenter(true);
		await removeLocalStorage(['handsock_uid', 'handsock_username', 'handsock_token', 'handsock_password']);
	}

	static sendClientSocketEmit = async ({ event, data, callback }): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		if (interval === 0) {
			interval = 2;
			let intervalTimer = setInterval(() => {
				interval--;
				if (interval === 0) clearInterval(intervalTimer);
			}, 1000);
			applicationStore.socketIo.emit(event, data, async (response: restfulType): Promise<void> => {
				if (response.code === 403) {
					await HandUtils.removeClientLoginStatus();
				} else await callback(response);
			});
		} else utils.showToasts('warning', '操作频率过快');
	}

	static scrollToChatBottom = () => {
		setTimeout(() => {
			const chatContentBox = document.querySelector(".chat-content-box");
			if (chatContentBox) {
				chatContentBox.scrollTo({
					top: chatContentBox.scrollHeight,
					behavior: 'smooth'
				});
			}
		}, 100);
	}

	static resetOnlineUsers = async (status: any) => {
		const applicationStore = utils.useApplicationStore();
		applicationStore.socketIo.emit(
			socket.send.OnlineLogin, { 
				status: status,
				platform: HandUtils.getClientPlatform(),
				uid: localStorage.getItem("handsock_uid") 
			}, () => {}
		);
	}

	static playNotificationSound = async (messageObject: messageType): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		if (applicationStore.userInfo.uid !== messageObject.uid) {
			if (messageObject.type === 'text' || messageObject.type === 'clap') {
				if (localStorage.getItem('audio-switch') === 'true') await playNoticeVoice();
				if (localStorage.getItem('audio-switch') !== 'true' && localStorage.getItem('audio-switch') !== 'false') {
					await playNoticeVoice();
					localStorage.setItem('audio-switch', 'true');
				}
			}
		}
	}

	static openUserLogoutDialog = async (): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		await HandUtils.checkClientLoginStatus((): void => {
			utils.showConfirms('none', '系统提示', '确定要退出登录吗，你的聊天记录将会丢失？', async (): Promise<void> => {
				await HandUtils.resetOnlineUsers(0);
				applicationStore.setLoginStatus(false);
				applicationStore.userInfo.avatar = applicationStore.defaultAvatar;
				await removeLocalStorage(['handsock_uid', 'handsock_token', 'handsock_username', 'handsock_password']);
				applicationStore.socketIo.emit("[USER:LOGOUT]", null);
				utils.showToasts('success', '退出登录成功');
			}, '取消', '退出登录');
		});
	}

	static checkClientVersion = async (): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		applicationStore.socketIo.emit(socket.send.ClientCheck, {
			version: socket.application.appVersion
		}, (response: restfulType): void => {
			if (response.code === 200) return;
			HandUtils.showDialogFireBySwal({
				icon: "error",
				title: "前后端版本不一致",
				text: "请将前端和后端更新到同一个版本"
			})
		});
	}

	static searchChannelByGid = async (router: any, channel: any): Promise<void> => {
		setSearchDialog(false);
		const applicationStore = utils.useApplicationStore();
		if (!/^[-+]?\d+$/.test(channel)) return utils.showToasts('warning', '格式不合规');
		const channelId = Number(channel);
		if (applicationStore.groupInfo.gid === channelId) return utils.showToasts('warning', '重复加入频道');
		await HandUtils.sendClientSocketEmit({
			event: socket.send.Search.SearchGroup,
			data: {
				gid: channelId
			},
			callback: (response: restfulType): void => {
				if (response.code === 200) {
					const { gid, name } = response.data;
					utils.showConfirms('none', '搜索频道', `查找到频道【${name}】，是否加入？`, async (): Promise<void> => {
						await HandUtils.toggleChatChannel(router, gid);
						utils.showToasts('success', "加入频道成功");
					}, '取消', '进入频道');
				} else utils.showToasts('warning', '未找到该频道');
			}
		});
	}

	static initChatUserList = async (): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		applicationStore.socketIo.emit(socket.send.Search.SearchUserAll, null, (response: restfulType): void => {
			applicationStore.userList = response.data
		});
	}
	
	static initChatGroupList = async (): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		applicationStore.socketIo.emit(socket.send.Search.SearchGroupAll, null, (response: restfulType) :void => {
			applicationStore.chatGroupList = response.data;
		});
	}

	static initChatGroup = async (): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		const channelId = new URLSearchParams(location.search).get("channel");
		applicationStore.socketIo.emit(socket.send.Search.SearchGroup, {
			gid: (channelId === null || !/^[-+]?\d+$/.test(channelId)) ? 0 : Number(channelId)
		}, (response: restfulType): void => {
			if (response.code === 200) {
				document.title = response.data.name;
				applicationStore.setGroupClosed(false);
				applicationStore.groupInfo = response.data;
				applicationStore.groupInfo.aiRole = response.data.aiRole === 1;
				HandUtils.initChatHistoryList(response.data.aiRole == 1);
			} else {
				document.title = "对应频道未开放";
				applicationStore.setGroupClosed(true);
			}
		});
	}
	
	static initChatHistoryList = async (aiRole: boolean): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		applicationStore.messageList.length = 0;
		if (aiRole) return;
		const channelId = new URLSearchParams(location.search).get("channel");
		applicationStore.socketIo.emit(socket.send.Search.SearchHistoryAll, {
			gid: (channelId === null || !/^[-+]?\d+$/.test(channelId)) ? 0 : Number(channelId)
		}, (response: restfulType): any => {
			if (response.code !== 200) return utils.showToasts('error', response.message);
			applicationStore.messageList = response.data;
			setTimeout(() => document.querySelector(".chat-content-box").scrollTo({ top: document.querySelector(".chat-content-box").scrollHeight, behavior: 'smooth' }), 100);
		});
	}

	static sendChatMessage = async (): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		const messageContent = applicationStore.chantInput.trim();
		if (!messageContent) return utils.showToasts('warning', '消息内容不能为空');
	
		const sendMessage = async (type: string, event: string, callback?: (response: restfulType) => void) => {
			await HandUtils.sendClientSocketEmit({
				event: event,
				data: {
					type: 'text',
					content: messageContent
				},
				callback: async (response: restfulType): Promise<void> => {
					if (response.code !== 200) return utils.showToasts('error', response.message);
					if (callback) callback(response);
				}
			});
		};
	
		if (applicationStore.groupInfo.aiRole) {
			await sendMessage(socket.send.SendAIChatMessage, socket.send.SendAIChatMessage, (response: restfulType) => {
				applicationStore.aiMessageList.push(response.data);
				HandUtils.scrollToChatBottom();
			});
		} else await sendMessage(socket.send.SendMessage, socket.send.SendMessage);
		applicationStore.setChantInput("");
	};

	static toggleConnectStatus = async (func: any) => {
		toggleStatus = true;
		const applicationStore = utils.useApplicationStore();
		applicationStore.socketIo.disconnect().connect();
		toggleStatus = false;
		Promise.all(func);
	}
	
	static toggleChatChannel = async (router: any, gid: any): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		await HandUtils.checkClientLoginStatus(async () => {
			if (applicationStore.groupInfo.gid !== gid) {
				router.push('/?channel=' + gid);
				applicationStore.socketIo.auth = (token: any): any => {
					token({
						gid: gid,
						token: HandUtils.getClientToken(),
						uid: applicationStore.userInfo.uid
					});
				}
				await HandUtils.toggleConnectStatus([
					await HandUtils.initChatGroup(),
				]);
			}
		});
	}

	static handleUserLogin = async (response: restfulType, password: string) => {
		const applicationStore = utils.useApplicationStore();
        if (response.code !== 200) return utils.showToasts('error', response.message);
		const onelDialogStore = utils.useOnelDialogStore();
        onelDialogStore.setUserLoginCenter(false);
        utils.showToasts('success', response.message);
        
        const { userinfo, token } = response.data;
        await saveLocalStorage([
            "handsock_uid",
            "handsock_token", 
            "handsock_username",
            "handsock_password"
        ], [userinfo.uid, token, userinfo.username, password]).then(async () => {
            setTimeout(async (): Promise<void> => {
                await HandUtils.toggleConnectStatus([]);
                await HandUtils.resetOnlineUsers(1);
            }, 300);
            applicationStore.userInfo = userinfo;
        });
    }
}

export default HandUtils;
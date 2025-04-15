import ua2obj from "ua2obj";
import Swal from "sweetalert2";
import utils from "@/scripts/utils";
import socket from "../socket/socket";
import AudioUtils from "./AudioUtils";
import StorageUtils from "./StorageUtils";
import { startSocketIo } from "@/socket/socketClient";
import { useApplicationStore } from "@/stores/applicationStore";
import { groupInfoType, messageType, restfulType, userAuthStatus, userInfoType } from "../../types";

let interval = 0;

export default class HandUtils {
	static getUserPermission(): number {
		const applicationStore = useApplicationStore();
		return applicationStore.userInfo.permission;
	}

	static getClientPlatform(): String {
		const userAgentObj = ua2obj();
        return `${userAgentObj.osName} ${userAgentObj.osVersion}`;
	}

	static getImageUrl = (content: string): string => {
		return socket.server.config.serverUrl + socket.server.downloadImages + content;
	}

	static getClientToken = (): string => {
		return localStorage.getItem("handsock_token");
	}

	static getUserTypeByInfo = (userinfo: userInfoType): string => {
		switch (userinfo.permission) {
			case 1:
				return "admin";
			case 2:
				return "robot";
			default:
				return "normal";
		}
	}

	static previewImageBySwal = ({ src, html }): void => {
		Swal.fire({
			imageWidth: "90%",
			showConfirmButton: false,
			html: html, imageUrl: src,
		}).then(() => {});
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
		}).then(() => {});
	}

	static removeClientLoginStatus = async (): Promise<void> => {
		const onelDialogStore = utils.useOnelDialogStore();
		const applicationStore = utils.useApplicationStore();
		applicationStore.resetUserInfo();
		utils.showToasts('error', '请重新登录');
		onelDialogStore.setUserLoginCenter(true);
		await StorageUtils.removeLocalStorage([
			'handsock_uid', 'handsock_username', 'handsock_token', 'handsock_password'
		]);
	}

	static sendClientSocketEmit = async ({ event, data, callback }): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		if (interval === 0) {
			interval = 2;
			let intervalTimer = setInterval(() => {
				interval--;
				if (interval === 0) clearInterval(intervalTimer);
			}, 1000);
			applicationStore.socketIo.emit(event, data, async (response: restfulType<any>): Promise<void> => {
				if (response.code === 403) {
					await HandUtils.removeClientLoginStatus();
				} else await callback(response);
			});
		} else utils.showToasts('warning', '操作频率过快');
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
				if (localStorage.getItem('audio-switch') === 'true') await AudioUtils.playNoticeVoice();
				if (localStorage.getItem('audio-switch') !== 'true' && localStorage.getItem('audio-switch') !== 'false') {
					await AudioUtils.playNoticeVoice();
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
				applicationStore.resetUserInfo();
				applicationStore.setLoginStatus(false);
				applicationStore.userInfo.avatar = applicationStore.defaultAvatar;
				await StorageUtils.removeLocalStorage(['handsock_uid', 'handsock_token', 'handsock_username', 'handsock_password']);
				applicationStore.socketIo.emit("[USER:LOGOUT]", null);
				utils.showToasts('success', '退出登录成功');
				await startSocketIo();
			}, '取消', '退出登录');
		});
	}

	static checkClientVersion = async (): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		applicationStore.socketIo.emit(socket.send.ClientCheck, {
			version: socket.application.appVersion
		}, (response: restfulType<any>): void => {
			if (response.code === 200) return;
			HandUtils.showDialogFireBySwal({
				icon: "error",
				title: "前后端版本不一致",
				text: "请将前端和后端更新到同一个版本"
			})
		});
	}

	static searchChannelByGid = async (channel: any): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		if (!/^[-+]?\d+$/.test(channel)) return utils.showToasts('warning', '格式不合规');
		const channelId = Number(channel);
		if (applicationStore.groupInfo.gid === channelId) return utils.showToasts('warning', '重复加入频道');
		await HandUtils.sendClientSocketEmit({
			event: socket.send.Search.SearchGroup,
			data: { gid: channelId },
			callback: (response: restfulType<groupInfoType>): void => {
				if (response.code === 200) {
					const { gid, name, active } = response.data;
					utils.showConfirms('none', '搜索频道', `查找到频道【${name}】，是否加入？`, async (): Promise<void> => {
						if (active && !applicationStore.chatGroupList.some(item => item.gid === gid)) applicationStore.chatGroupList.push(response.data);
						utils.showToasts('success', "加入频道成功");
					}, '取消', '进入频道');
				} else utils.showToasts('warning', '未找到该频道');
			}
		});
	}

	static sendChatMessage = async (): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		const messageContent = applicationStore.chantInput.trim();
		if (!messageContent) return utils.showToasts('warning', '消息内容不能为空');
	
		const sendMessage = async (type: string, event: string, callback?: (response: restfulType<any>) => void) => {
			await HandUtils.sendClientSocketEmit({
				event: event,
				data: {
					type: 'text',
					content: messageContent
				},
				callback: async (response: restfulType<any>): Promise<void> => {
					if (response.code !== 200) return utils.showToasts('error', response.message);
					if (callback) callback(response);
				}
			});
		};
	
		if (applicationStore.groupInfo.aiRole) {
			await sendMessage(socket.send.SendAIChatMessage, socket.send.SendAIChatMessage, (response: restfulType<any>) => {
				applicationStore.aiMessageList.push(response.data);
			});
		} else await sendMessage(socket.send.SendMessage, socket.send.SendMessage);
		applicationStore.setChantInput("");
	};
	
	static toggleChatChannel = async (router: any, gid: any): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		applicationStore.setForumStatus(false);
		await HandUtils.checkClientLoginStatus(async () => {
			if (applicationStore.groupInfo.gid.toString() !== gid.toString()) {
				await router.push(gid === 0 ? '/' : '/?channel=' + gid);
				await startSocketIo();
				if (applicationStore.chatList.some(item => item.gid.toString() === gid.toString())) {
					applicationStore.chatList.find(item => item.gid.toString() === gid.toString()).num = 0;
				}
			}
		});
	}

	static handleUserLogin = async (response: restfulType<userAuthStatus>, password: string) => {
		const applicationStore = utils.useApplicationStore();
        if (response.code !== 200) return utils.showToasts('error', response.message);
		const onelDialogStore = utils.useOnelDialogStore();
        onelDialogStore.setUserLoginCenter(false);
        utils.showToasts('success', response.message);
        
        const { userinfo, token } = response.data;
        await StorageUtils.saveLocalStorage([
            "handsock_uid", "handsock_token", "handsock_username", "handsock_password"
        ], [userinfo.uid, token, userinfo.username, password]).then(async () => {
			applicationStore.userInfo = userinfo;
            setTimeout(async (): Promise<void> => {
                // await HandUtils.resetOnlineUsers(1);
				await startSocketIo();
            }, 300);
        });
    }

	static openCustomSwalDialog(content: any, {
		title = "", props = {}, width = 525
	} = {}): void {
		HandUtils.checkClientLoginStatus(async () => {
			const wrapper = document.createElement('div');
			const dialogApp = createApp({
				setup() {
					return () => h(content, props)
				}
			});
			dialogApp.mount(wrapper);
			await Swal.fire({
				title: title,
				width: width,
				html: wrapper,
				showConfirmButton: false,
				willClose: () => {
					setTimeout(() => dialogApp.unmount(), 800);
				}
			});
		}).then(() => {});
	}
}
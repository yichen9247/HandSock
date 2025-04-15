/**
 * Application Store Module
 * 
 * Manages global application state using Pinia store
 * Handles user info, chat groups, messages, connection status and UI state
 */

import { Reactive } from "vue"
import utils from "@/scripts/utils"
import { defineStore } from "pinia"
import { groupInfoType, userInfoType, messageType, onlineUserType } from "../../types"

// Update mobile device status on window resize
onresize = (): void => {
    const isMobile: any = utils.isMobile();
    const applicationStore = useApplicationStore();
    applicationStore.setIsDeviceMobile(isMobile);
}

export const useApplicationStore = defineStore('applicationStore', () => {
    const defaultAvatar: string = "./image/default.png";
    const channelId: string = new URLSearchParams(location.search).get("channel");

    // User information state
    const userInfo: Reactive<userInfoType> = reactive({
        uid: localStorage.getItem("handsock_uid"),
        nick: null,
        status: 0,
        permission: 0,
        username: null,
        avatar: defaultAvatar
    });

    // Group information state 
    const groupInfo: Reactive<groupInfoType> = reactive({
        gid: (channelId === null || !/^[-+]?\d+$/.test(channelId)) ? 0 : Number(channelId),
        open: true,
        aiRole: false,
        active: false,
        name: "聊天室初始化中",
        avatar: defaultAvatar,
        notice: "暂未获取到通知公告",
    });

    const sendst: Ref<number> = ref(0);
    const chantInput: Ref<string> = ref("");
    const chatList: Reactive<Array<any>> = reactive([]);
    const userList: Reactive<Array<userInfoType>> = reactive([]);
    const messageList: Reactive<Array<messageType>> = reactive([]);
    const aiMessageList: Reactive<Array<messageType>> = reactive([]);
    const chatGroupList: Reactive<Array<groupInfoType>> = reactive([]);
    const onlineUserList: Reactive<Array<onlineUserType>> = reactive([]);

    const socketIo: Ref<any> = ref(null);
    const serverUUID: Ref<string> = ref(null);
    const connection: Ref<boolean> = ref(false);
    const groupClosed: Ref<boolean> = ref(false);
    const loginStatus: Ref<boolean> = ref(false);
    const forumStatus: Ref<boolean> = ref(false);
    const isDeviceMobile: Ref<any> = ref(utils.isMobile());

    const setSendSt = (value: number): number => sendst.value = value;
    const setSocketIo = (value: any): any => socketIo.value = value;
    const setChantInput = (value: string): string => chantInput.value = value;
    const setServerUUID = (value: string): string => serverUUID.value = value;
    const setConnection = (state: boolean): boolean => connection.value = state;
    const setForumStatus = (value: boolean): boolean => forumStatus.value = value;
    const setLoginStatus = (value: boolean): boolean => loginStatus.value = value;
    const setGroupClosed = (value: boolean): boolean => groupClosed.value = value;
    const setIsDeviceMobile = (value: boolean): boolean => isDeviceMobile.value = value;

    const resetUserInfo = (): void => {
        userInfo.uid = "";
        userInfo.status = 0;
        userInfo.nick = null;
        userInfo.permission = 0;
        userInfo.username = null;
        userInfo.avatar = defaultAvatar;
    }

    // Return store properties and methods
    return ({ chatList, userInfo, serverUUID, isDeviceMobile, loginStatus, forumStatus, groupInfo, groupClosed, userList, messageList, aiMessageList, onlineUserList, connection, chantInput, socketIo, sendst, chatGroupList, setServerUUID, setIsDeviceMobile, setLoginStatus, setForumStatus, resetUserInfo, setGroupClosed, setConnection, setChantInput, setSocketIo, setSendSt, defaultAvatar });
});
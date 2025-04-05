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
    // Default values
    const defaultAvatar: string = "./image/default.png";
    const channelId: string = new URLSearchParams(location.search).get("channel");

    // User information state
    const userInfo: Reactive<userInfoType> = reactive({
        uid: localStorage.getItem("handsock_uid"),
        nick: null,
        isAdmin: 0,
        isRobot: 0,
        email: null, 
        username: null,
        avatar: defaultAvatar
    });

    // Group information state 
    const groupInfo: Reactive<groupInfoType> = reactive({
        gid: (channelId === null || !/^[-+]?\d+$/.test(channelId)) ? 0 : Number(channelId),
        open: true,
        aiRole: false,
        name: "聊天室初始化中",
        avatar: defaultAvatar,
        notice: "暂未获取到通知公告",
    });

    // Chat and message states
    const sendst: Ref<number> = ref(0);
    const chantInput: Ref<string> = ref("");
    const userList: Reactive<Array<userInfoType>> = reactive([]);
    const messageList: Reactive<Array<messageType>> = reactive([]);
    const aiMessageList: Reactive<Array<messageType>> = reactive([]);
    const chatGroupList: Reactive<Array<groupInfoType>> = reactive([]);
    const onlineUserList: Reactive<Array<onlineUserType>> = reactive([]);

    // Connection and system states
    const socketIo: Ref<any> = ref(null);
    const serverUUID: Ref<string> = ref(null);
    const connection: Ref<boolean> = ref(false);
    const groupClosed: Ref<boolean> = ref(false);
    const loginStatus: Ref<boolean> = ref(false);
    const isSiteReadyStatus: Ref<boolean> = ref(false);
    const isDeviceMobile: Ref<any> = ref(utils.isMobile());

    // State setters
    const setSendSt = (value: number): number => sendst.value = value;
    const setSocketIo = (value: any): any => socketIo.value = value;
    const setChantInput = (value: string): string => chantInput.value = value;
    const setServerUUID = (value: string): string => serverUUID.value = value;
    const setConnection = (state: boolean): boolean => connection.value = state;
    const setLoginStatus = (value: boolean): boolean => loginStatus.value = value;
    const setGroupClosed = (value: boolean): boolean => groupClosed.value = value;
    const setIsDeviceMobile = (value: boolean): boolean => isDeviceMobile.value = value;
    const setIsSiteReadyStatus = (value: boolean): boolean => isSiteReadyStatus.value = value;

    // Reset user information
    const resetUserInfo = (): void => {
        userInfo.uid = "";
        userInfo.nick = null;
        userInfo.email = null;
        userInfo.username = null;
        userInfo.avatar = defaultAvatar;
    }

    // Return store properties and methods
    return ({ userInfo, serverUUID, isDeviceMobile, isSiteReadyStatus, loginStatus, groupInfo, groupClosed, userList, messageList, aiMessageList, onlineUserList, connection, chantInput, socketIo, sendst, chatGroupList, setServerUUID, setIsDeviceMobile, setIsSiteReadyStatus, setLoginStatus, resetUserInfo, setGroupClosed, setConnection, setChantInput, setSocketIo, setSendSt, defaultAvatar });
});
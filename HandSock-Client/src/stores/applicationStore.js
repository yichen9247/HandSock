
import { reactive, ref } from "vue"
import utils from "@/scripts/utils"
import { defineStore } from "pinia"

onresize = async () => {
    let state = utils.isMobile();
    const applicationStore = useApplicationStore();
    applicationStore.setIsDeviceMobile(state);
}

export const useApplicationStore = defineStore('applicationStore',() => {
    const channelId = new URLSearchParams(location.search).get("channel");
    
    const userInfo = reactive({
        uid: localStorage.getItem("handsock_uid"), nick: null, email: null, username: null,
        avatar: "https://dn-qiniu-avatar.qbox.me/avatar/"
    });

    const groupInfo = reactive({
        gid: (channelId === null || !/^[-+]?\d+$/.test(channelId)) ? 0 : Number(channelId), name: "聊天室初始化中",
        avatar: "https://dn-qiniu-avatar.qbox.me/avatar/", notice: "暂未获取到通知公告", open: true
    });
    
    
    const sendst = ref(0);
    const chantInput = ref("");
    const userList = reactive([]);
    const messageList = reactive([]);
    const chatGroupList = reactive([]);
    const onlineUserList = reactive([]);
    
    const socketIo = ref(null);
    const serverUUID = ref(null);
    const connection = ref(false);
    const groupClosed = ref(false);
    const loginStatus = ref(false);
    const loginFormStatus = ref(false);
    const isSiteReadyStatus = ref(false);
    const isDeviceMobile = ref(utils.isMobile());

    const setSendSt = (value) => sendst.value = value;
    const setSocketIo = (value) => socketIo.value = value;
    const setChantInput = (value) => chantInput.value = value;
    const setConnection = (state) => connection.value = state;
    const setServerUUID = (value) => serverUUID.value = value;
    const setLoginStatus = (value) => loginStatus.value = value;
    const setGroupClosed = (value) => groupClosed.value = value;
    const setIsDeviceMobile = (value) => isDeviceMobile.value = value;
    const setLoginFormStatus = (value) => loginFormStatus.value = value;
    const setIsSiteReadyStatus = (value) => isSiteReadyStatus.value = value;

    const resetUserInfo = () => {
        userInfo.gid = 0;
        userInfo.nick = null;
        userInfo.email = null;
        userInfo.username = null;
        userInfo.avatar = "https://dn-qiniu-avatar.qbox.me/avatar/";
    }

    return ({ 
        userInfo, serverUUID, loginFormStatus,  isDeviceMobile, isSiteReadyStatus, loginStatus, groupInfo, groupClosed, userList, messageList, onlineUserList, connection, chantInput, socketIo, sendst, chatGroupList,
        setServerUUID, setIsDeviceMobile, setLoginFormStatus, setIsSiteReadyStatus, setLoginStatus, resetUserInfo, setGroupClosed, setConnection, setChantInput, setSocketIo, setSendSt
    });
});
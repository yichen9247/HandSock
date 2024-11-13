/* eslint-disable no-undef */

import { showFailToast } from "vant"
import { removeLocalStorage } from "./storageUtils"
import { checkLoginWork } from "@/socket/socketClient"
import { useOnelDialogStore } from "@/stores/onelDialogStore"
import { useApplicationStore } from "@/stores/applicationStore"

const isMobile = () => {
    return (navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i));
}

const backMainChannel = async () => {
    location.href = "/";
};

const showToasts = async (type, text) => {
    if (type === 'info') {
        !isMobile() && ElMessage({ message: text, type: 'info', plain: true });
    } else
        if (type === 'error') {
            isMobile() ? showFailToast(text) : ElMessage({ message: text, type: 'error', plain: true });
        } else
            if (type === 'success') {
                ElMessage({ message: text, type: 'success', plain: true });
            } else
                if (type === 'warning') {
                    !isMobile() && ElMessage({ message: text, type: 'warning', plain: true });
                }
}

const showErrorToasts = (type, text1, text2) => {
    if (!isMobile()) {
        if (type === 'error') ElMessage({ message: text2, type: 'error', plain: true });
        if (type === 'warning') ElMessage({ message: text2, type: 'warning', plain: true });
    } else showFailToast(text1);
}

const openUserLoginForms = async () => {
    const applicationStore = useApplicationStore();
    if (applicationStore.connection) {
        applicationStore.setLoginFormStatus(true)
    } else showErrorToasts('error', '服务器崩了', '与通信服务器的连接已断开！');
}

const openPersonalDialog = async () => {
    await checkLoginWork(() => {
        const onelDialogStore = useOnelDialogStore();
        onelDialogStore.setPersonalCenter(true);
    });
}

const openSearchDialog = async () => {
    await checkLoginWork(() => {
        const onelDialogStore = useOnelDialogStore();
        onelDialogStore.setSearchalCenter(true);
    });
}

const openSettingDialog = async () => {
    await checkLoginWork(() => {
        const onelDialogStore = useOnelDialogStore();
        onelDialogStore.setSettinglCenter(true);
    });
}

const openForMobileDrawer = async () => {
    await checkLoginWork(() => {
        const onelDialogStore = useOnelDialogStore();
        onelDialogStore.setMobilesidebars(true);
    });
};

const openGithubSite = () => window.open('https://github.com/yichen9247/HandSock');

const openAboutThisProject = () => {
    ElMessageBox.alert('HandSock 是一款有趣的聊天应用，基于 Mybatis-Plus，Springboot, Vue3 和 Socket.io，Redis 等技术开发', '关于项目', {
        confirmButtonText: '朕已知晓',
        callback: () => { },
    });
}

const queryUserInfo = (uid) => {
    const applicationStore = useApplicationStore();
    return applicationStore.userList.find(item => item.uid === uid);
}

const playNoticeVoice = async () => {
    const audio = localStorage.getItem('audio');
    const audioRef = document.getElementById("audioRef");

    let audioModule;
    switch (audio) {
        case 'apple':
            audioModule = import('@/assets/audio/apple.mp3');
            break;
        case 'momo':
            audioModule = import('@/assets/audio/momo.mp3');
            break;
        case 'huaji':
            audioModule = import('@/assets/audio/huaji.mp3');
            break;
        default:
            localStorage.setItem('audio', 'default');
            audioModule = import('@/assets/audio/default.mp3');
            break;
    }

    const audioFile = await audioModule;
    audioRef.src = audioFile.default;
    audioRef.load();
    if (!audioRef.paused) audioRef.pause();
    audioRef.play().catch(() => { });
}

const getClientToken = () => {
    return localStorage.getItem("handsock_token");
}

const uploadFileError = (data) => {
    if (data !== null && data.response !== null) showToasts('error', data.message);
}

export default { queryUserInfo, isMobile, showToasts, showErrorToasts, openAboutThisProject, playNoticeVoice, useApplicationStore, useOnelDialogStore, openGithubSite, backMainChannel, openUserLoginForms, openPersonalDialog, openSearchDialog, openSettingDialog, openForMobileDrawer, uploadFileError, getClientToken };
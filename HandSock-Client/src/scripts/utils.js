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

const returnLoadingSvg = () => {
    return `
        <path class="path" d="  
          M 30 15  
          L 28 17  
          M 25.61 25.61  
          A 15 15, 0, 0, 1, 15 30  
          A 15 15, 0, 1, 1, 27.99 7.5  
          L 15 15  
        " style="stroke-width: 4px; stroke: rgba(32, 165, 58, 1); fill: rgba(0, 0, 0, 0)"/>
    `;
}

const showToasts = async (type, text) => {
    if (type === 'info') {
        !isMobile() && ElMessage({ message: text, type: 'info', plain: true });
    } else 
    if (type === 'error') {
        isMobile() ? showFailToast(text) : ElMessage({ message: text, type: 'error' });
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

const openUserLogoutDialog = async () => {
    const applicationStore = useApplicationStore();
    await checkLoginWork(() => {
        ElMessageBox.confirm('确定要退出登录吗，你的聊天记录将会丢失？', '系统提示', {
            type: 'warning',
            cancelButtonText: '取消',
            confirmButtonText: '退出登录'
        }).then(async () => {
            applicationStore.setLoginStatus(false);
            await removeLocalStorage(['handsock_uid', 'handsock_token', 'handsock_username']);
            applicationStore.socketIo.emit("[USER:LOGOUT]", null);
            ElMessage({ message: '退出登录成功！', type: 'success' });
        }).catch(() => {});
    });
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
        callback: () => {},
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
    audioRef.play().catch(() => {});
}

const uploadFileError = (data) => {
    if (data !== null && data.response !== null) showToasts('error' , data.message);
}

const getClientToken = () => {
    return localStorage.getItem("handsock_token");
}

const audioMenu = { default: '默认', apple: '苹果', momo: '陌陌', huaji: '滑稽' };
const themeMenu = { default: '默认', refresh: '清爽', pureshs: '雅灰', yalansh: '雅蓝' };

export default { queryUserInfo, isMobile, audioMenu, themeMenu, showToasts, showErrorToasts, openAboutThisProject, playNoticeVoice, useApplicationStore, useOnelDialogStore, returnLoadingSvg, openUserLogoutDialog, openGithubSite, backMainChannel, openUserLoginForms, openPersonalDialog, openSearchDialog, openSettingDialog, openForMobileDrawer, uploadFileError, getClientToken };
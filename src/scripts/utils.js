/* eslint-disable no-undef */

import { showFailToast } from "vant"
import { useApplicationStore } from "@/stores/application"
import { useOnelDialogStore } from "@/stores/onelDialogStore"
import { useOnlineChatStore } from "@/stores/onlineChatStore"

const isMobile = () => {
    if (navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)) {
        return true;
    } else return false;
}

const generUserId = () => {
    return Math.floor(111111111 + Math.random() * 9999999999);
}

const aboutThisChatPage = () => {
    ElMessageBox.alert('HandSock 是一款有趣的聊天应用，基于 Node.js, Vue3, Mysql 和 Socket.io，Redis 等技术开发', '关于项目', {
        confirmButtonText: '朕已知晓',
        callback: () => {},
    });
}

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
        !isMobile() && ElMessage({ message: text, type: 'info' });
    } else 
    if (type === 'error') {
        isMobile() ? showFailToast(text) : ElMessage({ message: text, type: 'error' });
    } else 
    if (type === 'success') {
        ElMessage({ message: text, type: 'success' });
    } else 
    if (type === 'warning') {
        !isMobile() && ElMessage({ message: text, type: 'warning' });
    }
}

const showErrorToasts = async (type, text1, text2) => {
    if (!isMobile()) {
        if (type === 'error') ElMessage({ message: text2, type: 'error' });
        if (type === 'warning') ElMessage({ message: text2, type: 'warning' });
    } else showFailToast(text1);
}

const audioMenu = { default: '默认', apple: '苹果', momo: '陌陌', huaji: '滑稽' };
const themeMenu = { default: '默认', refresh: '清爽', pureshs: '雅灰', yalansh: '雅蓝' };

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

export default { isMobile, audioMenu, themeMenu, showToasts, showErrorToasts, generUserId, aboutThisChatPage, playNoticeVoice, useApplicationStore, useOnelDialogStore, useOnlineChatStore, returnLoadingSvg };
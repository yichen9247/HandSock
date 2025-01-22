/**
 * HandSock Utility Functions
 * 
 * Core utility functions for HandSock chat application including:
 * - Device detection
 * - Toast notifications
 * - Navigation helpers
 * - User info queries
 * - File upload handling
 */

import Swal, { SweetAlertIcon } from 'sweetalert2'
import { ElMessage, ElMessageBox } from "element-plus"
import { messageType, userInfoType } from '../../types'
import { useOnelDialogStore } from "@/stores/onelDialogStore"
import { useApplicationStore } from "@/stores/applicationStore"
import { showFailToast, showSuccessToast, showToast, showConfirmDialog } from "vant"

const isMobile = (): RegExpMatchArray => navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i);

const showToasts = async (type: string, text: string): Promise<void> => {
    if (type === 'info') {
        isMobile() ? showToast(text) : ElMessage({ message: text, type: 'info', plain: true });
    } else if (type === 'error') {
        isMobile() ? showFailToast(text) : ElMessage({ message: text, type: 'error', plain: true });
    } else if (type === 'success') {
        isMobile() ? showSuccessToast(text) : ElMessage({ message: text, type: 'success', plain: true });
    } else if (type === 'warning') {
        isMobile() ? showFailToast(text) : ElMessage({ message: text, type: 'warning', plain: true });
    }
}

const showConfirms = async (type: any, title: string, message: string, callback: Function, cancelButtonText: string, confirmButtonText: string): Promise<void> => {
    isMobile()? showConfirmDialog({
        title: title,
        message: message,
        cancelButtonText: cancelButtonText,
        confirmButtonText: confirmButtonText,
        confirmButtonColor: 'var(--dominColor)',
    }).then(async () => {
        await callback();
    }).catch(() => {}):  
    ElMessageBox.confirm( message, title, {
        type: type,
        cancelButtonText: cancelButtonText,
        confirmButtonText: confirmButtonText
    }).then(async () => {
        await callback();
    }).catch(() => {});
}

const queryUserInfo = (uid: string): userInfoType => {
    const applicationStore = useApplicationStore();
    return applicationStore.userList.find(item => item.uid === uid);
}

const queryMessageInfo = (sid: string): messageType => {
    const applicationStore = useApplicationStore();
    return applicationStore.messageList.find(item => item.sid === sid);
}

const getMessageType = (type: string, content: string): string => {
    return type === 'text' ? content : type === 'image' ? '图片消息' : type === 'file' ? '文件消息' : '未知消息';
}

const getClientToken = (): string => localStorage.getItem("handsock_token");
const uploadFileError = (data: any): any => showToasts('error', data.message);

const previewImage = ({ src, html }): void => {
    Swal.fire({
        imageWidth: "90%",
        showConfirmButton: false,
        html: html, imageUrl: src,
    });
}

const getFileSize = (fileSize: number, mode: string): number => {
    const fileSizeKB = fileSize / 1024.0;
    if (mode === 'KB') {
        return parseFloat(fileSizeKB.toFixed(2));
    } else if (mode === 'MB') {
        return parseFloat((fileSizeKB / 1024.0).toFixed(2));
    } else return null;
}

const showSwalFire = (icon: SweetAlertIcon, title: string, text: string): void => {
    Swal.fire({
        showConfirmButton: false,
        icon: icon, text: text, title: title,
        allowEscapeKey: false, allowOutsideClick: false
    });
}

export default { queryUserInfo, queryMessageInfo, getMessageType, previewImage, isMobile, showToasts, showConfirms, useApplicationStore, useOnelDialogStore, showSwalFire, uploadFileError, getClientToken, getFileSize };
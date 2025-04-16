import { ElMessage, ElMessageBox } from "element-plus"
import { useOnelDialogStore } from "@/stores/onelDialogStore"
import { useApplicationStore } from "@/stores/applicationStore"

const isMobile = (): RegExpMatchArray => navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i);

const showToasts = async (type: string, text: string): Promise<void> => {
    if (type === 'info') {
        ElMessage({ message: text, type: 'info', plain: true });
    } else if (type === 'error') {
        ElMessage({ message: text, type: 'error', plain: true });
    } else if (type === 'success') {
        ElMessage({ message: text, type: 'success', plain: true });
    } else if (type === 'warning') {
        ElMessage({ message: text, type: 'warning', plain: true });
    }
}

const showConfirms = async (type: any, title: string, message: string, callback: Function, cancelButtonText: string, confirmButtonText: string): Promise<void> => {
    ElMessageBox.confirm( message, title, {
        type: type,
        cancelButtonText: cancelButtonText,
        confirmButtonText: confirmButtonText
    }).then(async () => {
        await callback();
    }).catch(() => {});
}

const uploadFileError = (data: any): any => showToasts('error', data.message);

export default { isMobile, showToasts, showConfirms, useApplicationStore, useOnelDialogStore, uploadFileError };
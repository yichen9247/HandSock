import Swal from 'sweetalert2'
import socket from '@/socket/socket'
import { ElMessage } from "element-plus"
import HandUtils from '@/scripts/HandUtils'
import { useOnelDialogStore } from '@/stores/onelDialogStore'
import { useApplicationStore } from '@/stores/applicationStore'

export const toggleAdminFrame = async (): Promise<void> => {
    await HandUtils.checkClientLoginStatus(() => {
        const onelDialogStore = useOnelDialogStore();
        onelDialogStore.toggleAdminFrameStatus();
    });
}

export const setUserLoginForm = async (status: boolean): Promise<void> => {
    const applicationStore = useApplicationStore();
    if (applicationStore.connection) {
        const onelDialogStore = useOnelDialogStore();
        onelDialogStore.setUserLoginCenter(status)
    } else ElMessage({ message: "与通信服务器的连接已断开", type: 'error', plain: true });
}

export const openAboutThisProject = (): void => {
    Swal.fire({
        title: '关于项目',
        showConfirmButton: false,
        html: `<p style="font-size: 16px; line-height: 28px; word-break: break-all;">${socket.application.description}</p>`
    });
}

export const openGithubSite = (): Window => window.open(socket.application.github);
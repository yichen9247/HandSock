<!--
 * @Description: File upload dialog component for sending files in chat
 * @Author: Hua
 * @Date: 2024-11-25
 * @Features:
 *   - Drag and drop file upload
 *   - Multiple file support
 *   - Responsive dialog width
 *   - Upload status notifications
 *   - Automatic dialog close on upload
-->

<script setup lang="ts">
    import utils from "@/scripts/utils"
    import socket from "@/socket/socket"
    import { restfulType } from "../../../types"
    import { UploadFilled } from '@element-plus/icons-vue'
    import { sendSocketEmit } from "@/socket/socketClient"

    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    /**
     * Handle successful file upload
     * @param {Object} data - Upload response data
     */
    const uploadFileSuccess = async (data: any): Promise<void> => {
        if (data.code === 200) {
            utils.showToasts('success', data.message);
            await sendSocketEmit(socket.send.SendMessage, {
                type: "file",
                content: data.data.path
            }, (response: restfulType) => {
                if (response.code !== 200) utils.showToasts('error', data.message)
            });
        } else await utils.showToasts('error', data.message)
    }
</script>

<template>
    <el-dialog 
        class="emoje-center"
        v-model="onelDialogStore.uploadFileCenter"
        :width="applicationStore.isDeviceMobile ? '95%' : '500px'"
        title="发送文件" align-center destroy-on-close center
    >
        <el-upload
            class="upload-box"
            drag multiple
            @success="uploadFileSuccess"
            @error="utils.uploadFileError"
            @change="onelDialogStore.setUploadFileCenter(false)"
            :headers="{
                uid: applicationStore.userInfo.uid,
                gid: applicationStore.groupInfo.gid,
                token: utils.getClientToken()
            }"
            :action="socket.server.config.serverUrl + socket.server.uploadFile"
        >
            <el-icon class="el-icon--upload">
                <upload-filled />
            </el-icon>
            <div class="el-upload__text">点击此处上传文件</div>
        </el-upload>
    </el-dialog>
</template>

<style lang="less">
    div.upload-box {
        width: 100%;

        ul.el-upload-list {
            display: none;
        }
    }
</style>
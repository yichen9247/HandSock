
<script setup>
    import utils from "@/scripts/utils"
    import config from "@/scripts/config"
    const onelDialogStore = utils.useOnelDialogStore()
    const applicationStore = utils.useApplicationStore()
    import { UploadFilled } from '@element-plus/icons-vue'
    import { sendSocketEmit } from "@/socket/socketClient"

    const uploadFileSuccess = async (data) => {
        if (data !== null && data.response !== null) {
            if (data.code === 200) {
                utils.showToasts('success' , data.message);
                await sendSocketEmit("[SEND:MESSAGE]", {
                    type: "file",
                    content: data.data.path
                }, (response) => {
                    if (response.code !== 200) utils.showToasts('error' , data.message);
                });
            } else utils.showToasts('error' , data.message);
        }
    }
</script>

<template>
    <el-dialog class="emoje-center" v-model="onelDialogStore.uploadFileCenter" :width="applicationStore.isDeviceMobile ? '95%' : '500px'" title="发送文件" align-center destroy-on-close center>
        <el-upload class="upload-box" drag :action="config.serverAdress + config.serverApi.uploadFile" multiple @success="uploadFileSuccess" @error="utils.uploadFileError" @change="onelDialogStore.setUploadFileCenter(false)" :headers="{
            uid: applicationStore.userInfo.uid,
            gid: applicationStore.groupInfo.gid,
            token: utils.getClientToken()
        }">
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
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
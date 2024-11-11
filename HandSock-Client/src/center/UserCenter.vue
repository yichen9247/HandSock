
<script setup>
    import { ref } from 'vue'
    import utils from '@/scripts/utils'
    import config from '@/scripts/config'
    import { sendSocketEmit } from "@/socket/socketClient"
    import { EditPen, Check, Close } from '@element-plus/icons-vue'

    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();
    
    const editNickStatus = ref(false);
    const editNickValue = ref(applicationStore.userInfo.nick);

    const openNickEditStatus = () => {
        editNickStatus.value = true;
        editNickValue.value = applicationStore.userInfo.nick;
    };

    const handleSocketEmit = async (event, data, model) => {
        data.uid = applicationStore.userInfo.uid;
        await sendSocketEmit(event, data, (response) => {
            if (response.code === 200) {
                utils.showToasts('success', response.message);
                switch(model) {
                    case 'nick':
                        applicationStore.userInfo.nick = response.data.nick;
                        break;
                }
            } else utils.showToasts('error', response.message);
            editNickStatus.value = false;
        });
    };

    const validateLength = (value, minLength, maxLength, message) => {
        if ((value || '').length > maxLength || (value || '').length < minLength) {
            utils.showToasts('error', message);
            return false;
        }
        return true;
    };

    const emitToUpdateNick = async () => {
        if (!validateLength(editNickValue.value, 2, 10, "昵称长度不合规")) return;
        await handleSocketEmit("[EDIT:USER:NICK]", { nick: editNickValue.value }, 'nick');
    };

    const uploadFileSuccess = async (data) => {
        if (data !== null && data.response !== null) {
            if (data.code === 200) {
                await sendSocketEmit("[EDIT:USER:AVATAR]", {
                    path: data.data.path
                }, (response) => {
                    if (response.code !== 200) {
                        utils.showToasts('error' , data.message)
                    } else {
                        utils.showToasts('success' , data.message);
                        applicationStore.userInfo.avatar = data.data.path;
                    }
                });
            } else utils.showToasts('error' , data.message);
        }
    }
</script>

<template>
    <div class="handsock-user-center">
        <div class="userinfo-box">
            <div class="avatar-box">
                <el-upload ref="uploadRef" class="upload-avatar" :action="config.serverAdress + config.serverApi.uploadAvatar" @success="uploadFileSuccess" @error="utils.uploadFileError" @change="onelDialogStore.setUploadFileCenter(false)" :headers="{
                    uid: applicationStore.userInfo.uid,
                    gid: applicationStore.groupInfo.gid,
                    token: utils.getClientToken()
                }">
                    <template #trigger>
                        <div class="trigger" v-if="applicationStore.loginStatus && applicationStore.userList.length > 0 && applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid).isAdmin">管理员</div>
                        <el-image class="avatar" shape="square" size="small" :src="config.serverAdress + config.serverApi.downloadAvatar + applicationStore.userInfo.avatar"/>
                    </template>
                </el-upload>
                
            </div>
            <div class="nick-box" v-if="!editNickStatus">
                <span class="nick">{{ applicationStore.userInfo.nick }}</span>
                <el-tooltip effect="dark" content="修改昵称" placement="right">
                    <el-icon class="edit-icon" style="margin-left: 10px;" @click="openNickEditStatus">
                        <EditPen/>
                    </el-icon>
                </el-tooltip>
            </div>

            <div class="nick-box" v-else>
                <el-input class="edit-input" v-model="editNickValue" style="width: 145px" maxlength="10" show-word-limit/>
                <div class="button-sheet">
                    <el-button class="edit-icon" :icon="Check" circle size="small" type="success" @click="emitToUpdateNick"/>
                    <el-button class="edit-icon" :icon="Close" circle size="small" type="danger" @click="editNickStatus = false"/>
                </div>
            </div>
        </div>
        <el-alert class="notice-box" title="聊天室名称不允许重复，快来抢占" type="warning" :closable="false"/>
    </div>
</template>

<style>
    @import url("./styles/UserCenter.css");
</style>
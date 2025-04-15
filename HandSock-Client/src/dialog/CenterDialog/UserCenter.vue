<script setup lang="ts">
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import HandUtils from '@/scripts/HandUtils'
    import { EditPen, Check, Close } from '@element-plus/icons-vue'
    import {UserAuthType} from "../../../types/UserAuthType";

    const editNickStatus = ref(false);
    const applicationStore = utils.useApplicationStore();
    const editNickValue = ref(applicationStore.userInfo.nick);

    const openNickEditStatus = (): void => {
        editNickStatus.value = true;
        editNickValue.value = applicationStore.userInfo.nick;
    }

    const handleSocketEmit = async (event: string, data: any, model: string) => {
        data.uid = applicationStore.userInfo.uid
        await HandUtils.sendClientSocketEmit({
            data: data,
            event: event,
            callback: (response: any) => {
                if (response.code === 200) {
                    utils.showToasts('success', response.message)
                    if (model === 'nick') applicationStore.userInfo.nick = response.data.nick
                } else utils.showToasts('error', response.message)
                editNickStatus.value = false
            }
        });
    }

    const validateLength = (value: string, minLength: number, maxLength: number, message: string): boolean => {
        if ((value || '').length > maxLength || (value || '').length < minLength) {
            utils.showToasts('error', message);
            return false;
        }
        return true;
    }

    const emitToUpdateNick = async (): Promise<void> => {
        if (!validateLength(editNickValue.value, 2, 10, "昵称长度不合规")) return;
        await handleSocketEmit(socket.send.Edit.EditUserNick, { nick: editNickValue.value }, 'nick');
    }

    const uploadFileSuccess = async (data: any): Promise<void> => {
        if (data.code === 200) {
            await HandUtils.sendClientSocketEmit({
                event: socket.send.Edit.EditUserAvatar,
                data: {
                    path: data.data.path
                },
                callback: async (response: any): Promise<void> => {
                    if (response.code !== 200) {
                        await utils.showToasts('error', data.message)
                    } else {
                        await utils.showToasts('success', data.message)
                        applicationStore.userInfo.avatar = data.data.path
                    }
                }
            });
        } else await utils.showToasts('error', data.message)
    }
</script>

<template>
    <div class="handsock-user-center">
        <div class="userinfo-box">
            <div class="avatar-box">
                <el-upload ref="uploadRef" class="upload-avatar"
                    @success="uploadFileSuccess" @error="utils.uploadFileError"
                    :headers="{
                        token: HandUtils.getClientToken(),
                        uid: applicationStore.userInfo.uid,
                        gid: applicationStore.groupInfo.gid
                    }"
                    :action="socket.server.config.serverUrl + socket.server.uploadAvatar"
                >
                    <template #trigger>
                        <div class="trigger" v-if="HandUtils.getUserPermission() === UserAuthType.ADMIN_AUTHENTICATION">
                            管理员
                        </div>
                        <el-image class="avatar" shape="square" size="small"
                            :src="socket.server.config.serverUrl + socket.server.downloadAvatar + applicationStore.userInfo.avatar"
                        />
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
        <el-alert class="notice-box" type="warning" :closable="false"
            title="聊天室名称不允许重复，快来抢占" 
            :style="{ width: applicationStore.isDeviceMobile ? '100%' : '300px' }"
        />
    </div>
</template>

<style lang="less">
    @import url("./styles/UserCenter.less");
</style>
<!--
 * @Description: User settings component for managing profile information
 * @Author: Hua
 * @Date: 2024-11-25
 * @Features:
 *   - Update user nickname
 *   - Update username
 *   - Update password
 *   - Form validation
 *   - Real-time updates via socket
-->

<script setup lang="ts">
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import HandUtils from '@/scripts/HandUtils'
    import { restfulType } from '../../../types'

    const applicationStore = utils.useApplicationStore();

    const editForm = reactive({
        nick: '', username: '', password: ''
    });

    // Handle socket events and update store
    const handleSocketEmit = async (event: string, data: any, field: string): Promise<void> => {
        await HandUtils.sendClientSocketEmit({
            data: data,
            event: event,
            callback: (response: restfulType): void => {
            if (response.code === 200) {
                    utils.showToasts('success', response.message);
                    const { nick, avatar } = response.data;
                    if (field === 'nick') {
                        applicationStore.userInfo.nick = nick;
                    } else if (field === 'avatar') applicationStore.userInfo.avatar = avatar;
                } else utils.showToasts('error', response.message);
            }
        });
    };

    const validateLength = (value: string, minLength: number, maxLength: number, message: string) => {
        const length = (value || '').length;
        if (length < minLength || length > maxLength) {
            utils.showToasts('error', message);
            return false;
        }
        return true;
    };

    const emitToUpdateNick = async (): Promise<void> => {
        if (!validateLength(editForm.nick, 2, 10, "昵称长度不合规")) return;
        await handleSocketEmit(socket.send.Edit.EditUserNick, { nick: editForm.nick }, 'nick');
    };

    const emitToUpdateUsername = async (): Promise<void> => {
        if (!validateLength(editForm.username, 5, 20, "用户名长度不合规")) return;
        await handleSocketEmit(socket.send.Edit.EditUserName, { username: editForm.username }, 'username');
    };

    const emitToUpdatePassword = async (): Promise<void> => {
        if (!validateLength(editForm.password, 5, 50, "密码长度不合规")) return;
        await handleSocketEmit(socket.send.Edit.EditUserPassword, { password: editForm.password }, 'password');
    };
</script>

<template>
    <div class="user-setting">
        <p class="set-title">昵称设置</p>
        <div class="flex-line">
            <el-input v-model="editForm.nick" placeholder="请输入要设置的昵称" clearable/>
            <el-button type="primary" @click="emitToUpdateNick">设置昵称</el-button>
        </div>

        <p class="set-title">账号设置</p>
        <div class="flex-line">
            <el-input v-model="editForm.username" placeholder="请输入要设置的用户名" clearable/>
            <el-button type="primary" @click="emitToUpdateUsername">设置账号</el-button>
        </div>
        
        <p class="set-title">密码设置</p>
        <div class="flex-line">
            <el-input v-model="editForm.password" placeholder="请输入要设置的密码" clearable/>
            <el-button type="primary" @click="emitToUpdatePassword">设置密码</el-button>
        </div>
    </div>
</template>

<style lang="less">
    div.user-setting {
        width: 100%;

        p.set-title {
            font-size: 16px;
        }

        div.flex-line {
            height: unset;
            display: flex;
            margin: 15px 0;
            justify-content: space-between;

            div.el-input {
                height: 38px;
                width: calc(100% - 110px);
            }

            button.el-button {
                width: 100px;
                height: 38px;
            }
        }
    }
</style>
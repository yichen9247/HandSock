
<script setup>
    import { reactive } from 'vue'
    import utils from '@/scripts/utils'
    import { sendSocketEmit } from "@/socket/socketClient"

    const applicationStore = utils.useApplicationStore();

    const editForm = reactive({
        nick: null,
        username: null,
        password: null,
    });

    const handleSocketEmit = async (event, data, model) => {
        await sendSocketEmit(event, data, (response) => {
            if (response.code === 200) {
                utils.showToasts('success', response.message);
                switch(model) {
                    case 'nick':
                        applicationStore.userInfo.nick = response.data.nick;
                        break;
                    case 'avatar':
                        applicationStore.userInfo.avatar = response.data.avatar;
                        break;
                }
            } else utils.showToasts('error', response.message);
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
        if (!validateLength(editForm.nick, 2, 10, "昵称长度不合规")) return;
        await handleSocketEmit("[EDIT:USER:NICK]", { nick: editForm.nick }, 'nick');
    };

    const emitToUpdateUsername = async () => {
        if (!validateLength(editForm.username, 5, 20, "用户名长度不合规")) return;
        await handleSocketEmit("[EDIT:USER:USERNAME]", { username: editForm.username }, 'username');
    };

    const emitToUpdatePassword = async () => {
        if (!validateLength(editForm.password, 5, 50, "密码长度不合规")) return;
        await handleSocketEmit("[EDIT:USER:PASSWORD]", { password: editForm.password }, 'password');
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
            <el-input v-model="editForm.username" placeholder="请输入重置的用户名" clearable/>
            <el-button type="primary" @click="emitToUpdateUsername">设置账号</el-button>
        </div>
        
        <p class="set-title">密码设置</p>
        <div class="flex-line">
            <el-input v-model="editForm.password" placeholder="请输入哟重置的密码" clearable/>
            <el-button type="primary" @click="emitToUpdatePassword">设置密码</el-button>
        </div>
    </div>
</template>

<style>
    @import url("./styles/UserSetting.css");
</style>
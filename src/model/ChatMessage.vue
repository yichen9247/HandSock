<!-- eslint-disable no-unused-vars -->
<!-- eslint-disable no-undef -->
<script setup>
    import utils from "@/scripts/utils"
    import { showFailToast } from 'vant'
    import channelList from "../scripts/channel.js"

    const onlineChatStore = utils.useOnlineChatStore();
    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    const chatMessage = defineProps({
        message: Object
    });
    const circleUrl = "https://dn-qiniu-avatar.qbox.me/avatar/";
    const emit = defineEmits(['avatarClaps','sendChatMessage','avtMouseup','avtMousedown']);

    const avatarClick = (userid) => {
        if (userid === onlineChatStore.userid) {
            if (onlineChatStore.logind && onlineChatStore.connection) {
                onelDialogStore.setPersonalCenter(true);
            } else {
                if (applicationStore.isDeviceMobile) {
                    showFailToast('用户未登录');
                } else ElMessage({ message: '你还没有登录到聊天室哦！', type: 'warning' });
            }
        }
    }

    const avatarClaps = (sideName,userName) => emit('avatarClaps',userName,sideName);
</script>

<template>
    <span class="chat-message" v-if="message.channel === onlineChatStore.chatChannel" :data-type="message.code">
        <el-tag type="info" class="mx-1" effect="dark" v-if="message.code === 101">{{ message.message }}</el-tag>
        <el-tag type="success" class="mx-1" v-if="message.code === 103">{{ message.message }}</el-tag>
        <div class="message-info" v-if="message.code === 201 || message.code === 203" :style="{ justifyContent: message.userid === onlineChatStore.userid && 'flex-end' }">
            <div class="avatar-info" :style="message.userqq !== 0 ? `background-image: url(https://q2.qlogo.cn/headimg_dl?dst_uin=${message.userqq}&spec=5)` : `background-image: url(${circleUrl})`" v-if="message.userid !== onlineChatStore.userid" @dblclick="avatarClaps(message.name,onlineChatStore.username)" @mousedown="emit('avtMousedown',message.name)" @mouseup="emit('avtMouseup')" @touchstart="emit('avtMousedown',message.name)" @touchend="emit('avtMouseup')"></div>
            <div class="content-box">
                <div :class="'user-info'" :style="{ justifyContent: message.userid == onlineChatStore.userid ? 'flex-end' : 'flex-start' }">
                    <span class="user-time" v-if="message.userid !== onlineChatStore.userid">{{ message.time }}</span>
                    <span class="user-name" v-if="message.userid !== onlineChatStore.userid">{{ message.name }}</span>
                    <span class="user-admi user-tags" v-if="channelList.find(item => item.id === onlineChatStore.chatChannel).admin === message.userid && message.userid !== onlineChatStore.userid">管理员</span>
                    <span class="user-admi user-tags" v-if="channelList.find(item => item.id === onlineChatStore.chatChannel).admin === message.userid && message.userid === onlineChatStore.userid">管理员</span>
                    <span class="user-robo user-tags" v-if="message.code === 203">机器人</span>
                    <span class="user-name" v-if="message.userid === onlineChatStore.userid">{{ message.name }}</span>
                    <span class="user-time" v-if="message.userid === onlineChatStore.userid">{{ message.time }}</span>
                </div>
                <div class="message-content chatCode-O" v-if="message.chatCode === 0" v-html="message.message"></div>
                <div class="message-content chatCode-T" v-if="message.chatCode === 1" v-html="message.message"></div>
            </div>
            <div class="avatar-info" :style="message.userqq !== 0 ? `background-image: url(https://q2.qlogo.cn/headimg_dl?dst_uin=${message.userqq}&spec=5)` : `background-image: url(${circleUrl})`" v-if="message.userid === onlineChatStore.userid" @click="avatarClick(message.userid)"></div>
        </div>
    </span>
</template>

<style>
    @import url("./styles/ChatMessage.css");
</style>
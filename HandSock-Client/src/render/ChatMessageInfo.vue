<!-- eslint-disable no-unused-vars -->

<script setup>
    import utils from "@/scripts/utils"
    import config from "@/scripts/config"
    import { checkLoginWork } from "@/socket/socketClient"
    
    let mousetime = 0, mousedown = null;
    const applicationStore = utils.useApplicationStore();
    const chatMessage = defineProps({ message: Object });

    const avatarClaps = async (sideName,userName) => {
        await checkLoginWork(() => {
            applicationStore.socketIo.emit("[SEND:MESSAGE]", {
                type: 'clap',
                content: `${ sideName } 拍了拍 ${userName}`
            }, (response) => {
                if (response.code !== 200) utils.showToasts('error', response.message);
            });
        });
    };

    const avtMouseup = () => {
        mousetime = 0;
        clearInterval(mousedown);
    };

    const avtMousedown = async (username) => {
        await checkLoginWork(() => {
            mousedown = setInterval(() => {
            if (mousetime >= 1) {
                    mousetime = 0;
                    clearInterval(mousedown);
                    applicationStore.setChantInput(applicationStore.chantInput + "@" + username + " ");
                } else mousetime++;
            },300);
        });
    };

    const avatarClick = async (userid) => {
        if (userid === applicationStore.userInfo.uid) utils.openPersonalDialog();
    }
</script>

<template>
    <div class="message-info" v-if="applicationStore.userList.some(item => item.uid === message.uid)" :style="{ justifyContent: message.uid === applicationStore.userInfo.uid ? 'flex-end' : 'flex-start' }">
        <div class="avatar-info" :style="`background-image: url(${config.serverAdress + config.serverApi.downloadAvatar + utils.queryUserInfo(message.uid).avatar})`" v-if="message.uid !== applicationStore.userInfo.uid" @dblclick="avatarClaps(applicationStore.userInfo.nick, utils.queryUserInfo(message.uid).nick)" @mousedown="avtMousedown(utils.queryUserInfo(message.uid).nick)" @mouseup="avtMouseup" @touchstart="avtMousedown(utils.queryUserInfo(message.uid).nick)" @touchend="avtMouseup"></div>
        <div class="content-box" :style="{ alignItems: message.uid === applicationStore.userInfo.uid ? 'flex-end' : 'flex-start' }">
            <div class="user-info" v-if="message.type === 'text' || message.type === 'file' || message.type === 'image'">
                <span class="user-time" v-if="message.uid !== applicationStore.userInfo.uid">{{ message.time.split(' ')[1] }}</span>
                <span class="user-name" v-if="message.uid !== applicationStore.userInfo.uid">{{ utils.queryUserInfo(message.uid).nick }}</span>
                <span class="user-admi user-tags" v-if="utils.queryUserInfo(message.uid).isAdmin === 1 && utils.queryUserInfo(message.uid).isRobot !== 1">管理员</span>
                <span class="user-robo user-tags" v-if="utils.queryUserInfo(message.uid).isRobot === 1">机器人</span>
                <span class="user-name" v-if="message.uid === applicationStore.userInfo.uid">{{ utils.queryUserInfo(message.uid).nick }}</span>
                <span class="user-time" v-if="message.uid === applicationStore.userInfo.uid">{{ message.time.split(' ')[1] }}</span>
            </div>
            <div :class="`message-content ${message.uid === applicationStore.userInfo.uid ? 'chatCode-T' : 'chatCode-O'}`" v-text="message.content" v-if="message.type === 'text'"></div>
            <div :class="`message-content ${message.uid === applicationStore.userInfo.uid ? 'chatCode-T' : 'chatCode-O'}`" v-if="message.type === 'file'" style="padding: 0;">
                <ChatFilesMessage :message="message"/>
            </div>
            <div :class="`message-content ${message.uid === applicationStore.userInfo.uid ? 'chatCode-T' : 'chatCode-O'}`" v-if="message.type === 'image'" style="padding: 0;">
                <ChatPngMessage :message="message"/>
            </div>
        </div>
        <div class="avatar-info" :style="`background-image: url(${config.serverAdress + config.serverApi.downloadAvatar + utils.queryUserInfo(message.uid).avatar})`" v-if="message.uid === applicationStore.userInfo.uid" @click="avatarClick(message.uid)"></div>
    </div>
</template>
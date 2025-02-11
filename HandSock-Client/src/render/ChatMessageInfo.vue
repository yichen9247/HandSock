<script setup>
    import utils from "@/scripts/utils"
    import socket from "@/socket/socket"
    import { setPersonalDialog } from "@/scripts/action"
    import { checkLoginWork } from "@/socket/socketClient"
    
    let mousetime = 0;
    let mousedown = null;

    const applicationStore = utils.useApplicationStore();
    const chatMessage = defineProps({
        message: { type: Object, required: true }
    });

    /**
     * Sends a "clap" message when user double clicks another user's avatar
     */
    const avatarClaps = async (sideName, userName) => {
        await checkLoginWork(() => {
            applicationStore.socketIo.emit("[SEND:MESSAGE]", {
                type: 'clap',
                content: `${sideName} 拍了拍 ${userName}`
            }, (response) => {
                if (response.code !== 200) {
                    utils.showToasts('error', response.message);
                }
            });
        });
    };

    /**
     * Clears mouse hold timer when mouse is released
     */
    const avtMouseup = () => {
        mousetime = 0;
        clearInterval(mousedown);
    };

    /**
     * Starts mouse hold timer for @mention functionality
     */
    const avtMousedown = async (username) => {
        await checkLoginWork(() => {
            mousedown = setInterval(() => {
                if (mousetime >= 1) {
                    mousetime = 0;
                    clearInterval(mousedown);
                    applicationStore.setChantInput(applicationStore.chantInput + "@" + username + " ");
                } else {
                    mousetime++;
                }
            }, 300);
        });
    };
</script>

<template>
    <div class="message-info" 
         v-if="applicationStore.userList.some(item => item.uid === message.uid)" 
         :style="{ justifyContent: message.uid === applicationStore.userInfo.uid ? 'flex-end' : 'flex-start' }">
        
        <!-- Avatar for other users -->
        <div class="avatar-info" 
             v-if="message.uid !== applicationStore.userInfo.uid"
             :style="`background-image: url(${socket.server.config.serverUrl + socket.server.downloadAvatar + utils.queryUserInfo(message.uid).avatar})`"
             @dblclick="avatarClaps(applicationStore.userInfo.nick, utils.queryUserInfo(message.uid).nick)"
             @mousedown="avtMousedown(utils.queryUserInfo(message.uid).nick)"
             @mouseup="avtMouseup"
             @touchstart="avtMousedown(utils.queryUserInfo(message.uid).nick)"
             @touchend="avtMouseup">
        </div>

        <div class="content-box" 
             :style="{ alignItems: message.uid === applicationStore.userInfo.uid ? 'flex-end' : 'flex-start' }">
            
            <!-- Message header with user info -->
            <div class="user-info" 
                 v-if="message.type === 'text' || message.type === 'file' || message.type === 'image'">
                <span class="user-time" v-if="message.uid !== applicationStore.userInfo.uid">
                    {{ message.time.split(' ')[1] }}
                </span>
                <span class="user-name" v-if="message.uid !== applicationStore.userInfo.uid">
                    {{ utils.queryUserInfo(message.uid).nick }}
                </span>
                <span class="user-admi user-tags" 
                      v-if="utils.queryUserInfo(message.uid).isAdmin === 1 && utils.queryUserInfo(message.uid).isRobot !== 1">
                    管理员
                </span>
                <span class="user-robo user-tags" 
                      v-if="utils.queryUserInfo(message.uid).isRobot === 1">
                    机器人
                </span>
                <span class="user-name" v-if="message.uid === applicationStore.userInfo.uid">
                    {{ utils.queryUserInfo(message.uid).nick }}
                </span>
                <span class="user-time" v-if="message.uid === applicationStore.userInfo.uid">
                    {{ message.time.split(' ')[1] }}
                </span>
            </div>

            <!-- Message content -->
            <div :class="`message-content ${message.uid === applicationStore.userInfo.uid ? 'chatCode-T' : 'chatCode-O'}`"
                 v-html="message.content" 
                 v-if="message.type === 'text'">
            </div>

            <div :class="`message-content ${message.uid === applicationStore.userInfo.uid ? 'chatCode-T' : 'chatCode-O'}`"
                 v-if="message.type === 'file'" 
                 style="padding: 0;">
                <ChatFilesMessage :message="message"/>
            </div>

            <div :class="`message-content ${message.uid === applicationStore.userInfo.uid ? 'chatCode-T' : 'chatCode-O'}`"
                 v-if="message.type === 'image'" 
                 style="padding: 0;">
                <ChatPngMessage :message="message"/>
            </div>
        </div>

        <!-- Avatar for current user -->
        <div class="avatar-info"
             v-if="message.uid === applicationStore.userInfo.uid" 
             :style="`background-image: url(${socket.server.config.serverUrl + socket.server.downloadAvatar + utils.queryUserInfo(message.uid).avatar})`"
             @click="setPersonalDialog(true)">
        </div>
    </div>
</template>
<script setup>
    import utils from "@/scripts/utils"
    import HandUtils from "@/scripts/HandUtils"
    import { setPersonalDialog } from "@/scripts/action"
    
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
        await HandUtils.checkClientLoginStatus(() => {
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
        await HandUtils.checkClientLoginStatus(() => {
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

    const meeesgaeInfo = computed(() => {
        return {
            'userinfo': HandUtils.getUserInfoByUid(chatMessage.message.uid),
            'chatCode': chatMessage.message.uid === applicationStore.userInfo.uid,
        }
    });
</script>

<template>
    <div class="message-info" 
         v-if="applicationStore.userList.some(item => item.uid === message.uid)" 
         :style="{ justifyContent: message.uid === applicationStore.userInfo.uid ? 'flex-end' : 'flex-start' }">
        
        <!-- Avatar for other users -->
        <div class="avatar-info" 
             v-if="message.uid !== applicationStore.userInfo.uid"
             :style="`background-image: url(${HandUtils.getUserAvatarByPath(meeesgaeInfo.userinfo.avatar)})`"
             @dblclick="avatarClaps(applicationStore.userInfo.nick, meeesgaeInfo.userinfo.nick)"
             @mousedown="avtMousedown(meeesgaeInfo.userinfo.nick)"
             @mouseup="avtMouseup"
             @touchstart="avtMousedown(meeesgaeInfo.userinfo.nick)"
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
                    {{ meeesgaeInfo.userinfo.nick }}
                </span>
                <span class="user-admi user-tags" 
                      v-if="meeesgaeInfo.userinfo.isAdmin === 1 && meeesgaeInfo.userinfo.isRobot !== 1">
                    管理员
                </span>
                <span class="user-robo user-tags" 
                      v-if="meeesgaeInfo.userinfo.isRobot === 1">
                    机器人
                </span>
                <span class="user-name" v-if="message.uid === applicationStore.userInfo.uid">
                    {{ meeesgaeInfo.userinfo.nick }}
                </span>
                <span class="user-time" v-if="message.uid === applicationStore.userInfo.uid">
                    {{ message.time.split(' ')[1] }}
                </span>
            </div>

            <!-- Message content -->
            <div :class="`message-content ${meeesgaeInfo.chatCode ? 'chatCode-T' : 'chatCode-O'}`"
                 v-html="message.content" 
                 v-if="message.type === 'text'">
            </div>

            <div :class="`message-content ${meeesgaeInfo.chatCode ? 'chatCode-T' : 'chatCode-O'}`"
                 v-if="message.type === 'file'" 
                 style="padding: 0;">
                <ChatFilesMessage :message="message"/>
            </div>

            <div :class="`message-content ${meeesgaeInfo.chatCode ? 'chatCode-T' : 'chatCode-O'}`"
                 v-if="message.type === 'image'" 
                 style="padding: 0;">
                <ChatPngMessage :message="message"/>
            </div>
        </div>

        <!-- Avatar for current user -->
        <div class="avatar-info"
             v-if="message.uid === applicationStore.userInfo.uid" 
             :style="`background-image: url(${HandUtils.getUserAvatarByPath(meeesgaeInfo.userinfo.avatar)})`"
             @click="setPersonalDialog(true)">
        </div>
    </div>
</template>
<script setup lang="ts">
    import { computed } from 'vue'
    import utils from "@/scripts/utils"
    import socket from '@/socket/socket'
    import HandUtils from "@/scripts/HandUtils"
    import ChatPngMessage from './ChatPngMessage.vue'
    import ChatFilesMessage from './ChatFilesMessage.vue'
    import { messageType, restfulType } from '../../types'
    import UserCenter from "@/dialog/CenterDialog/UserCenter.vue"

    const applicationStore = utils.useApplicationStore();
    const props = defineProps({ message: { type: Object as PropType<messageType>, required: true } });
    const isCurrentUser = computed(() => props.message.uid === applicationStore.userInfo.uid && applicationStore.loginStatus);

    const messageInfo = computed(() => ({
        isSelf: isCurrentUser.value,
        user: HandUtils.getUserInfoByUid(props.message.uid),
        typeClass: isCurrentUser.value ? 'chatCode-T' : 'chatCode-O'
    }));

    const mouseState = reactive({
        timer: null,
        duration: 0
    });

    const handleAvatarInteraction = (username: string) => ({
        async mousedown() {
            await HandUtils.checkClientLoginStatus((): void => {
                mouseState.timer = setInterval(() => {
                    if (mouseState.duration++ >= 1) {
                        applicationStore.setChantInput(`${applicationStore.chantInput}@${username} `)
                        resetMouseState()
                    }
                }, 300);
            });
        },
        mouseup: resetMouseState,
        touchend: resetMouseState
    });

    const resetMouseState = (): void => {
        mouseState.duration = 0
        clearInterval(mouseState.timer)
    }

    const avatarClaps = async (sideName: string, userName: string): Promise<void> => {
        await HandUtils.checkClientLoginStatus(async (): Promise<void> => {
            await HandUtils.sendClientSocketEmit({
                event: socket.send.SendMessage,
                data: {
                    type: 'clap',
                    content: `${sideName} 拍了拍 ${userName}`
                },
                callback: (response: restfulType<any>) => {
                    if (response.code !== 200) utils.showToasts('error', response.message);
                }
            });
        });
    };
</script>

<template>
    <div class="message-info" 
        v-if="applicationStore.userList.some(u => u.uid === message.uid)" 
        :style="{ justifyContent: isCurrentUser ? 'flex-end' : 'flex-start' }"
    >
        <div class="avatar-info" v-if="!isCurrentUser"
            :type="HandUtils.getUserTypeByInfo(messageInfo.user)" 
            v-on="handleAvatarInteraction(messageInfo.user.nick)"
            @dblclick="avatarClaps(applicationStore.userInfo.nick, messageInfo.user.nick)"
        >
            <div class="avatar image-bg" v-lazy:background-image="HandUtils.getUserAvatarByPath(messageInfo.user.avatar)"></div>
        </div>

        <div class="content-box" :style="{ alignItems: isCurrentUser ? 'flex-end' : 'flex-start' }">
            <div class="user-info" v-if="['text', 'file', 'image'].includes(message.type)">
                <template v-if="!isCurrentUser">
                    <span class="user-time">{{ message.time.split(' ')[1] }}</span>
                    <span class="user-name">{{ messageInfo.user.nick }}</span>
                </template>
                <template v-else>
                    <span class="user-name">{{ messageInfo.user.nick }}</span>
                    <span class="user-time">{{ message.time.split(' ')[1] }}</span>
                </template>
            </div>
            <div class="message-content" v-if="message.type === 'file'" style="padding: 0;">
                <ChatFilesMessage :message="message"/>
            </div>

            <div class="message-content" v-if="message.type === 'image'" style="padding: 0;">
                <ChatPngMessage :message="message"/>
            </div>
            <div class="message-content" v-html="message.content" v-if="message.type === 'text'"></div>
        </div>

        <div class="avatar-info" v-if="isCurrentUser" :type="HandUtils.getUserTypeByInfo(messageInfo.user)"
            @click="HandUtils.openCustomSwalDialog(UserCenter, {
                width: 450
            })" 
        >
            <div class="avatar image-bg" v-lazy:background-image="HandUtils.getUserAvatarByPath(messageInfo.user.avatar)"></div>
        </div>
    </div>
</template>

<style lang="less">
    span.el-tag {
        background: #ffffff
    }

    div.content-frame.frame-mobile {
        span.el-tag {
            border: none;
            color: rgba(60, 60, 67, 0.65);
        }
    }
</style>
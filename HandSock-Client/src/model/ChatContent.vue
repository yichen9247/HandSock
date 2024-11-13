<script setup>
    import utils from '@/scripts/utils'
    import { useRouter } from 'vue-router'
    import Message from "@/model/ChatMessage.vue"
    import { toggleChatChannel } from '@/socket/socketClient'

    const router = useRouter();
    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <div :class="`content-frame ${applicationStore.isDeviceMobile ? 'frame-mobile' : 'frame-pc' }`">
        <div class="chat-content-box">
            <el-main class="content-main" v-if="applicationStore.groupClosed || !applicationStore.groupInfo.open" style="margin: auto;">
                <el-result icon="error" title="该频道未开启" style="height: 100%;" class="empty-box">
                    <template #extra>
                        <el-button type="primary" @click="toggleChatChannel(router, 0)">返回到主频道</el-button>
                    </template>
                </el-result>
            </el-main>
            <Message v-for="(message, index) in applicationStore.messageList" :key="index" :message="message" v-else/>
        </div>
        <ChatFootBox/>
    </div>
</template>

<style>
    @import url("./styles/ChatContent.css");
</style>
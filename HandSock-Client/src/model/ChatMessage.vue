<script setup lang="ts">
    import { messageType } from "../../types"
    import HandUtils from "@/scripts/HandUtils"
    
    const chatMessage = defineProps({
        message: { type: Object as PropType<messageType>, required: true }
    });
</script>

<template>
    <span class="chat-message" :data-type="message.type">
        <el-tag type="success" class="mx-1" effect="plain" v-if="message.type === 'clap'">
            {{ chatMessage.message.content }}
        </el-tag>
        <ChatMessageInfo :message="chatMessage.message"
            v-if="HandUtils.getUserInfoByUid(chatMessage.message.uid) !== null"
        />
    </span>
</template>

<style lang="less">
    @import url("./styles/ChatMessage.less");
</style>
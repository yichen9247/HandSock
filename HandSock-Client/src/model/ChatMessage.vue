<script setup>
    import utils from "@/scripts/utils"
    const chatMessage = defineProps({ message: Object });
    import ChatMessageInfo from "@/render/ChatMessageInfo.vue"
</script>

<template>
    <span class="chat-message" :data-type="message.type">
        <el-tag type="info" class="mx-1" v-if="chatMessage.message.type == 'join'" effect="plain">{{ chatMessage.message.content }}</el-tag>
        <el-tag type="success" class="mx-1" v-if="message.type == 'clap'" effect="plain">{{ chatMessage.message.content }}</el-tag>
        <ChatMessageInfo :message="chatMessage.message" v-if="utils.queryUserInfo(chatMessage.message.uid) !== undefined"/>
    </span>
</template>

<style>
    @import url("./styles/ChatMessage.css");
</style>
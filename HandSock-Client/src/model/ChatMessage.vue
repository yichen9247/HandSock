<!--
 * @Description: Chat message component that displays different types of messages (join, clap) with appropriate styling
 * @Props: 
 *   message: Object - Contains message data including type, content and user info
 * @Components:
 *   el-tag - Element Plus tag component for displaying message content
 *   ChatMessageInfo - Component to show user information
-->

<script setup lang="ts">
    import utils from "@/scripts/utils"
    
    const chatMessage = defineProps({
        message: { type: Object, required: true }
    });
</script>

<template>
    <span class="chat-message" :data-type="message.type">
        <!-- Join message -->
        <el-tag 
            v-if="chatMessage.message.type === 'join'"
            type="info" 
            class="mx-1" 
            effect="plain"
        >
            {{ chatMessage.message.content }}
        </el-tag>

        <!-- Clap message -->
        <el-tag 
            v-if="message.type === 'clap'"
            type="success" 
            class="mx-1" 
            effect="plain"
        >
            {{ chatMessage.message.content }}
        </el-tag>

        <!-- User info if available -->
        <ChatMessageInfo 
            v-if="utils.queryUserInfo(chatMessage.message.uid) !== null"
            :message="chatMessage.message"
        />
    </span>
</template>

<style lang="less">
    @import url("./styles/ChatMessage.less");
</style>
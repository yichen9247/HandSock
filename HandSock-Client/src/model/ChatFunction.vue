<script setup>
    import utils from '@/scripts/utils'
    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <div class="chat-function">
        <div class="head-box">在线人数：{{ applicationStore.onlineUserList.length }}</div>
        <div class="function-box">
            <van-notice-bar left-icon="volume-o" :text="applicationStore.groupInfo.notice"/>
            <div class="online-list">
                <ChatOnlineUser v-for="(item, index) in applicationStore.onlineUserList" :key="index" :user="item"/>
            </div>
        </div>
        <el-button class="report-button" type="primary" @click="utils.showToasts('warning', '当前功能正在开发中')">举报聊天记录</el-button>
    </div>
</template>

<style>
    @import url("./styles/ChatFunction.css");
</style>
<script setup>
    import utils from '@/scripts/utils'
    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <div class="chat-function">
        <div class="head-box">在线人数：{{ applicationStore.onlineUserList.length }}</div>
        <div class="function-box">
            <van-notice-bar left-icon="volume-o" :text="applicationStore.groupInfo.notice"/>
        </div>
    </div>
</template>

<style>
    @import url("./styles/ChatFunction.css");
</style>
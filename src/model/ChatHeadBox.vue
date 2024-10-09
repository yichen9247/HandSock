
<script setup>
    import utils from "@/scripts/utils.js"
    import channelList from "../scripts/channel.js"
    import { Menu } from '@element-plus/icons-vue'

    const onlineChatStore = utils.useOnlineChatStore();
    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    const openMobileDrawer = () => {
        if (onlineChatStore.logind && onlineChatStore.connection) {
            onelDialogStore.setMobilesidebars(true);
        } else utils.showErrorToasts('warning', '用户未登录', '你还没有登录到聊天室哦！');
    };
    document.title = channelList.find(item => item.id === onlineChatStore.chatChannel).name;
</script>

<template>
    <el-header class="content-header">
        <el-button class="h-button" v-if="applicationStore.isDeviceMobile" :icon="Menu" @click="openMobileDrawer"></el-button>
        <span class="headerText" v-text="channelList.some(item => item.id === onlineChatStore.chatChannel) ? channelList.find(item => item.id === onlineChatStore.chatChannel).name : channelList.find(item => item.id === 0).name"></span>
        <el-tooltip class="box-item" effect="dark" :content="`连接状态：${onlineChatStore.connection ? '在线' : '离线'}`" placement="right" v-if="!applicationStore.isDeviceMobile">
            <span class="systemStat" :style="{ backgroundColor: onlineChatStore.connection ? 'var(--dominColor)' : '#F56C6C' }"></span>
        </el-tooltip>
        <span class="onlineText" v-if="!applicationStore.isDeviceMobile">在线人数：{{ onlineChatStore.onlineUser }}</span>
    </el-header>
</template>

<style>
    @import url("./styles/ChatHeadBox.css");
</style>
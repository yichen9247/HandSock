<script setup>
    
    import { ref, watch } from 'vue'
    import utils from "@/scripts/utils.js"
    import ChatFootBox from './ChatFootBox.vue'
    import ChatHeadBox from './ChatHeadBox.vue'
    import Message from "@/model/ChatMessage.vue"
    import channelList from "../scripts/channel.js"
    import socketClient from "@/socket/socketClient.js"

    const onlineChatStore = utils.useOnlineChatStore();
    const onelDialogStore = utils.useOnelDialogStore();
    
    const contents = ref(null);
    let mousetime = 0, mousedown = null;

    watch(onlineChatStore.messageList, (news) => {
        if (!onlineChatStore.connection) return;
        const message = news[news.length - 1];
        setTimeout(() => document.querySelector(".content-main").scrollTo({ top: document.querySelector(".content-main").scrollHeight, behavior: 'smooth' }),100);
        if ((onlineChatStore.userid === message.userid) || (onlineChatStore.chatChannel !== message.channel)) return;
        if (message.code === 201) {
            if (localStorage.getItem('audio-switch') === 'true') utils.playNoticeVoice();
            if (localStorage.getItem('audio-switch') !== 'true' && localStorage.getItem('audio-switch') !== 'false') {
                utils.playNoticeVoice();
                localStorage.setItem('audio-switch', true);
            }
        }
    }, { deep: true });

    const searchFromChannel = () => {
        if (onlineChatStore.logind && onlineChatStore.connection) {
            onelDialogStore.setSearchalCenter(true);
        } else utils.showErrorToasts('warning', '用户未登录', '你还没有登录到聊天室哦！');
    }
    
    const avatarClaps = async (sideName, userName) => await socketClient.sendChatMessage(103, sideName + ' 拍了拍 ' + userName);

    const backMainChannel = () => {
        history.pushState(null,null,"/");
        onlineChatStore.setChatChannel(0);
    };

    const avtMouseup = () => {
        mousetime = 0;
        clearInterval(mousedown);
    };

    const avtMousedown = (username) => {
        mousedown = setInterval(() => {
            if (mousetime >= 1) {
                mousetime = 0;
                clearInterval(mousedown);
                onlineChatStore.setChantInput(onlineChatStore.chantInput + "@" + username + " ");
            } else mousetime++;
        },300);
    };
    
    onlineChatStore.chatChannel !== 0 && utils.showToasts('warning', '当前为自定义频道模式，请遵守聊天规则！');
    const fastOptionDown = () => document.querySelector(".content-main").scrollTo({ top: document.querySelector(".content-main").scrollHeight, behavior: 'smooth' });
</script>

<template>
    <div id="chat-content">
        <audio id="audioRef" style="display: none!important;"></audio>
        <el-container>
            <ChatHeadBox/>
            <el-main class="content-main main-empty" ref="contents" v-if="onlineChatStore.messageList.length === 1 || !onlineChatStore.connection" v-loading="!onlineChatStore.connection" element-loading-text="正在从服务器获取数据" element-loading-background="#dae8f1" :element-loading-spinner="utils.returnLoadingSvg" element-loading-svg-view-box="-10, -10, 50, 50">
                <el-empty description=" " @click="searchFromChannel"/>
            </el-main>

            <el-main class="content-main" ref="contents" v-if="onlineChatStore.messageList.length > 1 && channelList.some(item => item.id === onlineChatStore.chatChannel) && onlineChatStore.connection" v-loading="!onlineChatStore.connection" element-loading-text="正在从服务器获取数据" element-loading-background="#dae8f1" :element-loading-spinner="utils.returnLoadingSvg" element-loading-svg-view-box="-10, -10, 50, 50">
                <el-button class="fast-option" type="warning" plain v-if="onlineChatStore.messageList.length > 10 && onlineChatStore.chatChannel === 0" @click="fastOptionDown">快速定位到最新的聊天记录</el-button>
                <Message v-for="(message, index) in onlineChatStore.messageList" :key="index" :message="message" @avatarClaps="avatarClaps" @avtMousedown="avtMousedown" @avtMouseup="avtMouseup"/>
            </el-main>

            <el-main class="content-main" ref="contents" v-if="!channelList.some(item => item.id === onlineChatStore.chatChannel)" v-loading="!onlineChatStore.connection" element-loading-text="正在从服务器获取数据" element-loading-background="#dae8f1" :element-loading-spinner="utils.returnLoadingSvg" element-loading-svg-view-box="-10, -10, 50, 50">
                <el-result icon="error" title="该频道未开启">
                    <template #extra>
                        <el-button type="primary" @click="backMainChannel(onlineChatStore.chatChannel)">返回到主频道</el-button>
                    </template>
                </el-result>
            </el-main>
            <ChatFootBox/>
        </el-container>
    </div>
</template>

<style>
    @import url("./styles/ChatContent.css");
</style>
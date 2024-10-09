
<script setup>
    import axios from "axios"
    import utils from "@/scripts/utils.js"
    import channelList from "../scripts/channel.js"
    import socketClient from "@/socket/socketClient.js"

    const onlineChatStore = utils.useOnlineChatStore();
    const applicationStore = utils.useApplicationStore();

    const sendChatMessage = async () => {
        if (!onlineChatStore.connection) return utils.showErrorToasts('error', '服务器崩了', '与通信服务器的连接已断开！');
        if (!channelList.some(item => item.id === onlineChatStore.chatChannel)) return utils.showErrorToasts('error', '频道未开启', '该频道暂未开启！');
        if (onlineChatStore.chantInput === "") return utils.showErrorToasts('warning', '格式不正确', '请参照正确的聊天内容格式！');
        const regex = /# (.*)/gm;
        if (channelList.find(item => item.id === onlineChatStore.chatChannel).admin === onlineChatStore.userid) {
            onlineChatStore.chantInput.match(regex) ? await socketClient.sendChatMessage(1, onlineChatStore.chantInput.replace(regex, '$1')) : await socketClient.sendChatMessage(0, onlineChatStore.chantInput);
        } else {
            onlineChatStore.chantInput.match(regex) ? await socketClient.sendChatMessage(1, onlineChatStore.chantInput.replace(regex, '$1')) : await socketClient.sendChatMessage(0, onlineChatStore.chantInput);
        }
        onlineChatStore.setChantInput("");
    }

    const sendChatHitokoto = () => {
        onlineChatStore.setSendSt(2);
        const sendText = async () => {
            try {
                const hitokoto = await axios.get('https://international.v1.hitokoto.cn/');
                await socketClient.sendChatMessage(0, hitokoto.data.hitokoto);
            } catch (e) {
                utils.showErrorToasts('error', '消息被拦截', '消息发送遭到阻拦！');
            } finally {
                onlineChatStore.setSendSt(0);
            }
        }
        sendText();
    }

    const handleKeydown = async (event) => (event.ctrlKey && event.key === "Enter") && await sendChatMessage();
</script>

<template>
    <el-footer class="content-footer">
        <el-input autosize type="textarea" class="chat-input" v-model="onlineChatStore.chantInput" placeholder="请在此输入聊天内容，按Ctrl+Enter发送..." clearable maxlength="100" show-word-limit @keydown="handleKeydown"/>
        <el-button class="chat-button" type="primary" plain @click="sendChatMessage">{{ onlineChatStore.sendst === 1 ? '发送消息（发送中）' : '发送消息' }}</el-button>
        <el-button class="more-button" type="primary" plain @click="sendChatHitokoto" v-loading="onlineChatStore.sendst === 2" element-loading-background="#dae8f1" :element-loading-spinner="utils.returnLoadingSvg" element-loading-svg-view-box="-10, -10, 50, 50" v-if="!applicationStore.isDeviceMobile">
            <svg t="1708769523336" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5067" width="20" height="20"><path d="M925.696 384q19.456 0 37.376 7.68t30.72 20.48 20.48 30.72 7.68 37.376q0 20.48-7.68 37.888t-20.48 30.208-30.72 20.48-37.376 7.68l-287.744 0 0 287.744q0 20.48-7.68 37.888t-20.48 30.208-30.72 20.48-37.376 7.68q-20.48 0-37.888-7.68t-30.208-20.48-20.48-30.208-7.68-37.888l0-287.744-287.744 0q-20.48 0-37.888-7.68t-30.208-20.48-20.48-30.208-7.68-37.888q0-19.456 7.68-37.376t20.48-30.72 30.208-20.48 37.888-7.68l287.744 0 0-287.744q0-19.456 7.68-37.376t20.48-30.72 30.208-20.48 37.888-7.68q39.936 0 68.096 28.16t28.16 68.096l0 287.744 287.744 0z" p-id="5068" fill="#ffffff"></path></svg>
        </el-button>
    </el-footer>
</template>

<style>
    @import url("./styles/ChatFootBox.css");
</style>
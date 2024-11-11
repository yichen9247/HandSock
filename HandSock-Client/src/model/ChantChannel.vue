<script setup>
    import utils from '@/scripts/utils'
    import { useRouter } from 'vue-router'
    import { toggleChatChannel } from '@/socket/socketClient'
    
    const router = useRouter();
    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <div class="chat-channel">
        <div v-for="(item, index) in applicationStore.chatGroupList" :key="index" :class="`channel-item ${applicationStore.groupInfo.gid === item.gid ? 'item-active' : 'none'} `" @click="toggleChatChannel(router, item.gid)">
            <div class="item-content">
                <img class="avatar" :src="item.avatar" :alt="item.name">
                <div class="info-content">
                    <span class="item-name">{{ item.name }}
                    </span>
                    <span class="item-latest">暂时不做聊天记录处理</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style>
    @import url("./styles/ChantChannel.css");
</style>
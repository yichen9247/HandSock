<script setup lang="ts">
    import { computed } from 'vue'
    import utils from '@/scripts/utils'
    import { useRouter } from 'vue-router'
    import HandUtils from '@/scripts/HandUtils'
    
    const router = useRouter();
    const applicationStore = utils.useApplicationStore();
    const currentGroupId = computed(() => applicationStore.groupInfo.gid);
    const messageType: any = { file: '[文件消息]', image: '[图片消息]', clap: '[拍一拍消息]'};
    
    const chatMap = computed(() => new Map(
        applicationStore.chatList.map(item => [item.gid, item])
    ));
    
    const formatMessage = (chatItem?: any) => {
        if (!chatItem) return "暂时没有更多未读消息";
        const isSelf = chatItem.uid === applicationStore.userInfo.uid;
        const username = isSelf ? '我' : HandUtils.getUserInfoByUid(chatItem.uid)?.nick;
        const content = chatItem.type === 'text' ? chatItem.content : messageType[chatItem.type];
        return `${username}：${content}`;
    }
    const handleChannelClick = (gid: number) => HandUtils.toggleChatChannel(router, gid)
</script>

<template>
    <div class="chat-channel">
        <div :class="['channel-item', { 'item-active': currentGroupId === group.gid }]" v-for="group in applicationStore.chatGroupList" :key="group.gid" @click="handleChannelClick(group.gid)">
            <div class="item-content">
                <img class="avatar" v-lazy="group.avatar" :alt="group.name"/>
                <div class="info-content">
                    <span class="item-name">{{ group.name }}</span>
                    <span class="item-latest">{{ formatMessage(chatMap.get(group.gid)) }}</span>
                    <div class="message-number">
                        <span class="number swal2-show" v-if="chatMap.get(group.gid)?.num > 0">
                            {{ chatMap.get(group.gid)?.num }}
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="less">
    @import url("./styles/ChantChannel.less");
</style>
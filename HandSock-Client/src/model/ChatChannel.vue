<!--
 * @Description: Chat channel component that displays and manages channel switching
 * @Author: Hua
 * @Date: 2024-11-25
 * @Features:
 *   - Displays list of available chat channels
 *   - Shows channel avatar and name
 *   - Handles channel switching on click
 *   - Highlights active channel
-->

<script setup lang="ts">
    import utils from '@/scripts/utils'
    import { useRouter } from 'vue-router'
    import { toggleChatChannel } from '@/socket/socketClient'
    
    const router = useRouter();
    const applicationStore = utils.useApplicationStore();
    const currentGroupId = (): number => applicationStore.groupInfo.gid;
    const handleChannelClick = (gid: string): Promise<void> => toggleChatChannel(router, gid);
</script>

<template>
    <div class="chat-channel">
        <div 
            v-for="(item, index) in applicationStore.chatGroupList" 
            :key="index"
            :class="[
                'channel-item',
                {'item-active': currentGroupId() === item.gid}
            ]"
            @click="handleChannelClick(item.gid.toString())"
        >
            <div class="item-content">
                <img 
                    class="avatar" 
                    :alt="item.name"
                    :src="item.avatar" 
                />
                <div class="info-content">
                    <span class="item-name">{{ item.name }}</span>
                    <span class="item-latest">暂时不做聊天记录处理</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="less">
    @import url("./styles/ChantChannel.less");
</style>
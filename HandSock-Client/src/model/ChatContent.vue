<!--
 * @Description: Chat content component that displays messages and handles channel state
 * @Author: Hua
 * @Date: 2024-11-25
 * @Components:
 *   Message - Chat message component for rendering individual messages
 *   ChatFootBox - Footer component with input and controls
 *   el-main - Element Plus main container
 *   el-result - Element Plus result component for closed channel state
 *   el-button - Element Plus button for channel navigation
-->

<script setup lang="ts">
    import utils from '@/scripts/utils'
    import { Router, useRouter } from 'vue-router'
    import { toggleChatChannel } from '@/socket/socketClient'

    const router: Router = useRouter();
    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <div :class="`content-frame ${applicationStore.isDeviceMobile ? 'frame-mobile' : 'frame-pc'}`">
        <div class="chat-content-box">
            <!-- Show error state when channel is closed -->
            <el-main 
                class="content-main" 
                v-if="applicationStore.groupClosed || !applicationStore.groupInfo.open" 
                style="margin: auto;"
            >
                <el-result 
                    icon="error" 
                    title="该频道未开启" 
                    style="height: 100%;" 
                    class="empty-box"
                >
                    <template #extra>
                        <el-button 
                            type="primary" 
                            @click="toggleChatChannel(router, 0)"
                        >
                            返回到主频道
                        </el-button>
                    </template>
                </el-result>
            </el-main>

            <!-- Display message list when channel is open -->
            <ChatMessage 
                v-else
                v-for="(message, index) in applicationStore.messageList" 
                :key="index" 
                :message="message"
            />
            <el-empty style="margin: auto;" description="暂无更多消息记录" v-if="applicationStore.loginStatus && applicationStore.messageList.length === 0 && applicationStore.groupInfo.open" />
        </div>
        <ChatFootBox/>
    </div>
</template>

<style lang="less">
    @import url("./styles/ChatContent.less");

    .el-empty__description p {
        font-size: 18px;
    }
</style>
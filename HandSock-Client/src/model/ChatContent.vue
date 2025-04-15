<script setup lang="ts">
    import { computed } from 'vue'
    import utils from '@/scripts/utils'
    import HandUtils from '@/scripts/HandUtils'
    import { Router, useRouter } from 'vue-router'

    const router: Router = useRouter();
    const applicationStore = utils.useApplicationStore();

    const isGroupUnavailable = computed(() => 
        applicationStore.groupClosed || !applicationStore.groupInfo.open
    )
    const isAIChat = computed(() => applicationStore.groupInfo.aiRole)
    const hasMessages = computed(() => currentMessageList.value.length > 0)

    const currentMessageList = computed(() => 
        isAIChat.value ? applicationStore.aiMessageList : applicationStore.messageList
    )
    const showEmptyState = computed(() =>
        applicationStore.loginStatus && !hasMessages.value &&
        applicationStore.groupInfo.open && !isGroupUnavailable.value
    )
</script>

<template>
    <div :class="`content-frame ${applicationStore.isDeviceMobile ? 'frame-mobile' : 'frame-pc'}`">
        <div class="chat-content-box">
            <el-main class="content-main" style="margin: auto;" v-if="isGroupUnavailable">
                <el-result class="empty-box" style="height: 100%;" icon="error" title="该频道未开启">
                    <template #extra>
                        <el-button type="primary" @click="HandUtils.toggleChatChannel(router, 0)">
                            返回到主频道
                        </el-button>
                    </template>
                </el-result>
            </el-main>

            <template v-else>
                <div class="chat-list" v-if="hasMessages">
                    <ChatMessage v-for="(message, index) in currentMessageList" :key="index" :message="message"/>
                </div>
                <el-empty style="margin: auto;" v-if="showEmptyState" :description="`暂无更多${isAIChat ? 'AI' : ''}消息记录`"/>
            </template>
        </div>
    </div>
</template>

<style lang="less">
    @import url("./styles/ChatContent.less");

    .el-empty__description p {
        font-size: 18px;
    }
</style>
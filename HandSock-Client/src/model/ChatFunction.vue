<!--
 * @Description: 聊天功能组件，显示在线用户列表和群组通知
 * @Author: Hua
 * @Date: 2024-11-25
-->

<script setup lang="ts">
    import utils from '@/scripts/utils'
    import { setReportDialog } from '@/scripts/action'

    const applicationStore = utils.useApplicationStore()
    const onlineCount = computed((): number => applicationStore.onlineUserList.length);
</script>

<template>
    <div class="chat-function" :class="applicationStore.isDeviceMobile ? 'device-mobile' : 'device-pc'">
        <div class="head-box">
            在线人数：{{ onlineCount }}
        </div>
        <div class="function-box">
            <div class="function-main">
                <van-notice-bar 
                    class="mobile-bg"
                    left-icon="volume-o" 
                    :text="applicationStore.groupInfo.notice"
                />

                <div class="online-list">
                    <ChatOnlineBox
                        v-for="(item, index) in applicationStore.onlineUserList"
                        :key="index" :user="item"
                    />
                </div>
            </div>

            <div class="button-box">
                <el-button
                    type="primary"
                    class="report-button"
                    @click="setReportDialog(true)"
                >
                    举报聊天记录
                </el-button>
            </div>
        </div>
    </div>
</template>

<style lang="less">
    @import url("./styles/ChatFunction.less");
</style>
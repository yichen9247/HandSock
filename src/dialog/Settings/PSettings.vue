<script setup>
    import { ref } from "vue"
    import utils from "@/scripts/utils.js"
    import channelList from "../../scripts/channel.js"
    import History from "../../../cache/history.json"
    import ChatSetting from "@/dialog/SettingItem/ChatSetting.vue"
    import ThemeManner from "@/dialog/SettingItem/ThemeManner.vue"
    import { User, PieChart, Setting, ChatLineRound, Notification } from '@element-plus/icons-vue'

    const settingIndex = ref(1);
    const onlineChatStore = utils.useOnlineChatStore();
    const applicationStore = utils.useApplicationStore();
    const useHistoryNu = ref(Math.floor(History.length / 6));
    const handleSelect = (key) => {
        settingIndex.value = Number(key);
        switch(Number(key)) {
            case 1:
                useHistoryNu.value = Math.floor(History.length / 6);
                break;
            default:
                useHistoryNu.value = 0;
                break;
        }
    }

    const colors = [
        { color: '#6f7ad3', percentage: 0 },
        { color: '#1989fa', percentage: 20 },
        { color: '#5cb87a', percentage: 40 },
        { color: '#e6a23c', percentage: 60 },
        { color: '#f56c6c', percentage: 80 },
    ]
</script>

<template>
    <div class="flex-line" v-if="!applicationStore.isDeviceMobile">
        <el-row class="tac">
          <el-col :span="12">
            <el-menu default-active="1" class="el-menu-vertical-setting" @select="handleSelect">
                <el-menu-item index="1">
                    <el-icon><PieChart /></el-icon>
                    <span>系统概览</span>
                </el-menu-item>
                
                <el-menu-item index="2">
                    <el-icon><Setting /></el-icon>
                    <span>主题设置</span>
                </el-menu-item>

                <el-menu-item index="3">
                    <el-icon><ChatLineRound /></el-icon>
                    <span>聊天设置</span>
                </el-menu-item>

                <el-menu-item index="5" v-if="channelList.find(item => item.id === onlineChatStore.chatChannel).admin === onlineChatStore.userid">
                    <el-icon><User /></el-icon>
                    <span>管理后台</span>
                </el-menu-item>

                <el-menu-item index="4">
                    <el-icon><Notification /></el-icon>
                    <span>关于项目</span>
                </el-menu-item>
            </el-menu>
          </el-col>
        </el-row>

        <ul class="content">
            <li class="content-page" :style="{ marginTop: settingIndex === 1 ? 0 : '500px', position: settingIndex === 1 ? 'relative' : 'absolute' }">
                <div class="page-header">
                    <span class="page-title">系统概览</span>
                </div>
                <div class="page-content">
                    <div class="flex-line">
                        <el-progress type="dashboard" :percentage="useHistoryNu" :color="colors">
                            <template #default="{ percentage }">
                              <span class="percentage-value">{{ percentage }}%</span>
                              <span class="percentage-label">聊天记录占比</span>
                            </template>
                        </el-progress>

                        <div class="progress-content">
                            <span class="content-text">当前已经使用：{{ History.length }}条</span>
                            <span class="content-text">剩余记录配额：{{ 600 - History.length }}条</span>
                            <span class="content-text">聊天记录限额：600条</span>
                            <span class="content-text">TIPS：当配额已满时，系统会提前告知用户并自动清理聊天记录</span>
                        </div>
                    </div>
                </div>
            </li>

            <li class="content-page" :style="{ marginTop: settingIndex === 2 ? 0 : '500px', position: settingIndex === 2 ? 'relative' : 'absolute' }">
                <div class="page-header">
                    <span class="page-title">主题设置</span>
                </div>
                <div class="page-content">
                    <ThemeManner/>
                </div>
            </li>

            <li class="content-page" :style="{ marginTop: settingIndex === 3 ? 0 : '500px', position: settingIndex === 3 ? 'relative' : 'absolute' }">
                <div class="page-header">
                    <span class="page-title">聊天设置</span>
                </div>
                <div class="page-content">
                    <ChatSetting/>
                </div>
            </li>

            <li class="content-page" :style="{ marginTop: settingIndex === 4 ? 0 : '500px', position: settingIndex === 4 ? 'relative' : 'absolute' }">
                <div class="page-header">
                    <span class="page-title">关于项目</span>
                </div>
                <div class="page-content">
                    <el-text class="mx-1">
                        <p>HandSock 是一款有趣的聊天应用，基于 Node.js, Vue3, Mysql 和 Socket.io 等技术开发</p>
                        <br/>
                        <p>项目作者：<el-link type="primary" href="https://github.com/yichen9247">yichen9247（Hua）</el-link></p>
                        <br/>
                        <p>项目地址：<el-link type="primary" href="https://github.com/yichen9247/HandSock">https://github.com/yichen9247/HandSock</el-link></p>
                    </el-text>
                </div>
            </li>

            <li class="content-page" :style="{ marginTop: settingIndex === 5 ? 0 : '500px', position: settingIndex === 5 ? 'relative' : 'absolute' }">
                <div class="page-header">
                    <span class="page-title">管理后台</span>
                </div>
                <div class="page-content">
                    <el-empty description="此处正在努力施工中" />
                </div>
            </li>
        </ul>
    </div>
</template>

<style>
    @import url("./styles/PSettings.css");
</style>
<script setup>
    import { ref } from 'vue'
    import "./styles/MSettings.css"
    import utils from "@/scripts/utils"
    import History from "../../../cache/history.json"
    import ChatSetting from "@/dialog/SettingItem/ChatSetting.vue"
    import ThemeManner from "@/dialog/SettingItem/ThemeManner.vue"
    
    const active = ref(0);
    const applicationStore = utils.useApplicationStore();
    const useHistoryNu = ref(Math.floor(History.length / 6));

    const colors = [
        { color: '#6f7ad3', percentage: 0 },
        { color: '#1989fa', percentage: 20 },
        { color: '#5cb87a', percentage: 40 },
        { color: '#e6a23c', percentage: 60 },
        { color: '#f56c6c', percentage: 80 },
    ]
</script>

<template>
    <div id="m-settings" v-if="applicationStore.isDeviceMobile">
        <van-tabs v-model:active="active" animated>
            <van-tab title="系统概览">
                <div class="tab-h">系统概览</div>
                <div class="tab-b">
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
            </van-tab>
            <van-tab title="主题设置">
                <div class="tab-h">主题设置</div>
                <div class="tab-b">
                    <ThemeManner/>
                </div>
            </van-tab>
            <van-tab title="聊天设置">
                <div class="tab-h">聊天设置</div>
                <div class="tab-b">
                    <ChatSetting/>
                </div>
            </van-tab>
            <van-tab title="关于项目">
                <div class="tab-h">关于项目</div>
                <div class="tab-b">
                    <el-text class="mx-1">
                        <p>HandSock 是一款有趣的聊天应用，基于 Node.js, Vue3, Mysql，Redis 和 Socket.io 等技术开发</p>
                        <br/>
                        <p>项目作者：<el-link type="primary" href="https://github.com/yichen9247" target="_blank">yichen9247（Hua）</el-link></p>
                        <br/>
                        <p>项目地址：<el-link type="primary" href="https://github.com/yichen9247/HandSock" target="_blank">https://github.com/yichen9247/HandSock</el-link></p>
                    </el-text>
                </div>
            </van-tab>
        </van-tabs>
    </div>
</template>
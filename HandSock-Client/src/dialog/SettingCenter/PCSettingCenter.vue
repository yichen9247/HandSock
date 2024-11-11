<script setup>
    import { ref } from "vue"
    import utils from "@/scripts/utils.js"
    import AdminPage from "@/dialog/SettingDialog/AdminPage.vue"
    import PrivacyPage from "@/dialog/SettingDialog/PrivacyPage.vue"
    import UserSetting from "@/dialog/SettingDialog/UserSetting.vue"
    import ChatSetting from "@/dialog/SettingDialog/ChatSetting.vue"
    import ThemeSetting from "@/dialog/SettingDialog/ThemeSetting.vue"
    import { User, PieChart, Setting, ChatLineRound, Notification, Document } from '@element-plus/icons-vue'

    const settingIndex = ref(1);
    const applicationStore = utils.useApplicationStore();
    const handleSelect = (key) => settingIndex.value = Number(key);
</script>

<template>
    <div class="flex-line" v-if="!applicationStore.isDeviceMobile">
        <el-row class="tac">
          <el-col :span="12">
            <el-menu default-active="1" class="el-menu-vertical-setting" @select="handleSelect">
                <el-menu-item index="1">
                    <el-icon><PieChart /></el-icon>
                    <span>个人设置</span>
                </el-menu-item>
                
                <el-menu-item index="2">
                    <el-icon><Setting /></el-icon>
                    <span>主题设置</span>
                </el-menu-item>

                <el-menu-item index="3">
                    <el-icon><ChatLineRound /></el-icon>
                    <span>聊天设置</span>
                </el-menu-item>

                <el-menu-item index="5" v-if="applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid).isAdmin">
                    <el-icon><User /></el-icon>
                    <span>管理后台</span>
                </el-menu-item>

                <el-menu-item index="6">
                    <el-icon><Document /></el-icon>
                    <span>隐私政策</span>
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
                    <span class="page-title">账号设置</span>
                </div>
                <div class="page-content">
                    <UserSetting/>
                </div>
            </li>

            <li class="content-page" :style="{ marginTop: settingIndex === 2 ? 0 : '500px', position: settingIndex === 2 ? 'relative' : 'absolute' }">
                <div class="page-header">
                    <span class="page-title">主题设置</span>
                </div>
                <div class="page-content">
                    <ThemeSetting/>
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

            <li class="content-page" :style="{ marginTop: settingIndex === 5 ? 0 : '500px', position: settingIndex === 5 ? 'relative' : 'absolute' }" v-if="applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid).isAdmin">
                <div class="page-header">
                    <span class="page-title">管理后台</span>
                </div>
                <div class="page-content">
                    <AdminPage/>
                </div>
            </li>

            <li class="content-page" :style="{ marginTop: settingIndex === 6 ? 0 : '500px', position: settingIndex === 6 ? 'relative' : 'absolute' }">
                <div class="page-header">
                    <span class="page-title">隐私政策</span>
                </div>
                <div class="page-content">
                    <PrivacyPage/>
                </div>
            </li>

            <li class="content-page" :style="{ marginTop: settingIndex === 4 ? 0 : '500px', position: settingIndex === 4 ? 'relative' : 'absolute' }">
                <div class="page-header">
                    <span class="page-title">关于项目</span>
                </div>
                <div class="page-content">
                    <el-text class="mx-1">
                        <p>HandSock 是一款有趣的聊天应用，基于 Mybatis-Plus，Springboot, Vue3 和 Socket.io，Redis 等技术开发</p>
                        <br/>
                        <p style="display: flex; align-items: center; flex-wrap: wrap;">项目作者：<el-link type="primary" href="https://github.com/yichen9247" target="_blank">yichen9247（Hua）</el-link></p>
                        <br/>
                        <p style="display: flex; align-items: center; flex-wrap: wrap;">项目地址：<el-link type="primary" href="https://github.com/yichen9247/HandSock" target="_blank">https://github.com/yichen9247/HandSock</el-link></p>
                    </el-text>
                </div>
            </li>
        </ul>
    </div>
</template>

<style>
    @import url("./styles/PCSettingCenter.css");
</style>
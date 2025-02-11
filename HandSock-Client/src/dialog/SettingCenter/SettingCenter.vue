<!--
 * @Description: PC settings center component with user settings, theme settings, chat settings and more
 * @Author: Hua
 * @Date: 2024-11-25
 * @Features:
 *   - Responsive layout
 *   - Admin privilege control
 *   - Vertical menu navigation
 *   - Dynamic content switching
-->

<script setup lang="ts">
    import utils from "@/scripts/utils"
    import { 
        PieChart, 
        Setting, 
        ChatLineRound, 
        Notification, 
        Document,
        Mug
    } from '@element-plus/icons-vue'

    const settingIndex: Ref<number> = ref(1);
    const applicationStore = utils.useApplicationStore();

    const handleSelect = (key: string): number => settingIndex.value = Number(key);
</script>

<template>
    <div class="flex-line" v-if="!applicationStore.isDeviceMobile">
        <el-row class="tac">
          <el-col :span="12">
            <el-menu 
                default-active="1" 
                class="el-menu-vertical-setting" 
                @select="handleSelect"
            >
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

                <el-menu-item index="4">
                    <el-icon><Mug /></el-icon>
                    <span>背景音乐</span>
                </el-menu-item>

                <el-menu-item index="5">
                    <el-icon><Document /></el-icon>
                    <span>隐私政策</span>
                </el-menu-item>

                <el-menu-item index="6">
                    <el-icon><Notification /></el-icon>
                    <span>关于项目</span>
                </el-menu-item>
            </el-menu>
          </el-col>
        </el-row>

        <ul class="content">
            <li 
                class="content-page" 
                :style="{ 
                    marginTop: settingIndex === 1 ? 0 : '500px', 
                    position: settingIndex === 1 ? 'relative' : 'absolute' 
                }"
            >
                <div class="page-header">
                    <span class="page-title">账号设置</span>
                </div>
                <div class="page-content">
                    <UserSetting/>
                </div>
            </li>

            <li 
                class="content-page" 
                :style="{ 
                    marginTop: settingIndex === 2 ? 0 : '500px', 
                    position: settingIndex === 2 ? 'relative' : 'absolute' 
                }"
            >
                <div class="page-header">
                    <span class="page-title">主题设置</span>
                </div>
                <div class="page-content">
                    <ThemeSetting/>
                </div>
            </li>

            <li 
                class="content-page" 
                :style="{ 
                    marginTop: settingIndex === 3 ? 0 : '500px', 
                    position: settingIndex === 3 ? 'relative' : 'absolute' 
                }"
            >
                <div class="page-header">
                    <span class="page-title">聊天设置</span>
                </div>
                <div class="page-content">
                    <ChatSetting/>
                </div>
            </li>

            <li 
                class="content-page" 
                :style="{ 
                    marginTop: settingIndex === 4 ? 0 : '500px', 
                    position: settingIndex === 4 ? 'relative' : 'absolute' 
                }"
            >
                <div class="page-header">
                    <span class="page-title">背景音乐</span>
                </div>
                <div class="page-content">
                    <MusicPage/>
                </div>
            </li>

            <li 
                class="content-page" 
                :style="{ 
                    marginTop: settingIndex === 5 ? 0 : '500px', 
                    position: settingIndex === 5 ? 'relative' : 'absolute' 
                }"
            >
                <div class="page-header">
                    <span class="page-title">隐私政策</span>
                </div>
                <div class="page-content">
                    <PrivacyPage/>
                </div>
            </li>

            <li 
                class="content-page" 
                :style="{ 
                    marginTop: settingIndex === 6 ? 0 : '500px', 
                    position: settingIndex === 6 ? 'relative' : 'absolute' 
                }"
            >
                <div class="page-header">
                    <span class="page-title">关于项目</span>
                </div>
                <div class="page-content">
                    <AboutPage/>
                </div>
            </li>
        </ul>
    </div>
</template>

<style lang="less">
    @import url("./styles/SettingCenter.less");
</style>
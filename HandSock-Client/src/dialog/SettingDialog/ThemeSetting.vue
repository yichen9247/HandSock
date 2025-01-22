<!--
 * @Description: Theme settings component for managing application appearance
 * @Author: Hua
 * @Date: 2024-11-25
 * @Features:
 *   - Theme selection via radio buttons
 *   - Persistent theme storage
 *   - Real-time theme switching
 *   - Toast notifications
-->

<script setup lang="ts">
    import utils from "@/scripts/utils"
    import { setDeviceTheme } from "@/scripts/themeUtils"

    // Get saved theme from localStorage or use default
    const themeSelect = ref(localStorage.getItem('theme') || 'default');

    /**
     * Handle theme change event
     * @param {string} selectedTheme - The newly selected theme value
     */
    const themeSelectChange = async (selectedTheme: string): Promise<void> => {
        try {
            localStorage.setItem('theme', selectedTheme);
            setDeviceTheme();
            await utils.showToasts('success', '切换主题成功');
        } catch (error) {
            await utils.showToasts('error', '切换主题失败');
            console.error('Theme change failed:', error);
        }
    }
</script>

<template>
    <div class="theme-select">
        <p class="set-title">主题颜色</p>
        <el-radio-group 
            @change="themeSelectChange"
            v-model="themeSelect" size="large" class="theme-group"
        >
            <el-radio-button label="默认" value="default" />
            <el-radio-button label="清爽" value="refresh" />
            <el-radio-button label="雅灰" value="pureshs" />
            <el-radio-button label="雅蓝" value="yalansh" />
        </el-radio-group>
    </div>
</template>

<style lang="less">
    div.theme-select {
        width: 100%;

        p.set-title {
            font-size: 16px;
        }

        div.theme-group {
            width: 100%;
            margin: 5px 0;
            margin-top: 15px;
        
            label {
                flex: 1;
                width: 25%;

                span {
                    width: 100%;
                }
            }
        }
    }
</style>
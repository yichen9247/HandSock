<script setup>
    import { ref } from "vue"
    import utils from "@/scripts/utils"
    import theme from "@/scripts/themeUtils"
    const themeSelect = ref(localStorage.getItem('theme'));

    const themeSelectChange = async (event) => {
        localStorage.setItem('theme', event);
        await theme.setDeviceTheme();
        await utils.showToasts('success' ,'切换主题成功');
    }
</script>

<template>
    <div class="theme-select">
        <p class="set-title">主题颜色</p>
        <el-radio-group v-model="themeSelect" size="large" class="theme-group" @change="themeSelectChange">
            <el-radio-button label="默认" value="default" />
            <el-radio-button label="清爽" value="refresh" />
            <el-radio-button label="雅灰" value="pureshs" />
            <el-radio-button label="雅蓝" value="yalansh" />
        </el-radio-group>
    </div>
</template>

<style>
    @import url("./styles/ThemeManner.css");
</style>
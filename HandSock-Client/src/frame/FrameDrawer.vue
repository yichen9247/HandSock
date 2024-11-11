<script setup>
    import { Back } from '@element-plus/icons-vue'
    const config = defineProps({ title: String, show: Boolean });
    
    const option = defineEmits(['close']);
</script>

<template>
    <div class="admin-content-sidebar" :style="{ transform: config.show ? 'translateX(0px)' : 'translateX(320px)' }">
        <div class="header-box">
            <el-icon @click="option('close')"><Back /></el-icon>
            <p class="title-text">{{ config.title }}</p>
        </div>
        <el-divider />
        <div class="sidbar-content">
            <slot></slot>
        </div>
    </div>
</template>

<style>
    @import url("./styles/FrameDrawer.css");
</style>
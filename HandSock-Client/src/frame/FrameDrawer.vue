<script setup lang="ts">
    import { Back } from '@element-plus/icons-vue'
    const config = defineProps({ title: String, show: Boolean });
    
    const option = defineEmits(['close']);
</script>

<template>
    <div class="handsock-content-sidebar" :style="{ transform: config.show ? 'translateX(0px)' : 'translateX(360px)' }">
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

<style lang="less">
    @import url("./styles/FrameDrawer.less");
</style>
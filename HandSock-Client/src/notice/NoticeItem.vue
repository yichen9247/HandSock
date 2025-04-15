<script setup lang="ts">
    import { noticeBoardType } from '../../types'

    const config = defineProps({
        post: { type: Object as PropType<noticeBoardType>, required: true }
    });
</script>

<template>
    <div class="notice-post-item">
        <div class="post-head">
            <div class="post-info">
                <span class="post-title">{{ config.post.title }}</span>
            </div>
        </div>
        <p class="post-content">{{ config.post.content }}</p>
        <div class="post-footer">
            <span class="footer-text">平台管理员</span>
            <span class="footer-text">{{ config.post.time }}</span>
        </div>
    </div>
</template>

<style lang="less">
    @import url("./styles/NoticeItem.less");
</style>
<script setup lang="ts">
    import HandUtils from "@/scripts/HandUtils"
    import { forumPostType } from "../../types"

    const config = defineProps({
        post: { type: Object as PropType<forumPostType>, required: true }
    });

    const imageList = JSON.parse(config.post.image);
</script>

<template>
    <div class="forum-post-item">
        <div class="post-head">
            <div class="post-info">
                <div class="user-info">
                    <el-avatar 
                        class="avatar" :size="28"
                        :src="HandUtils.getUserAvatarByPath(config.post.user.avatar)"
                    />
                    <span class="username">{{ config.post.user.nick }}</span>
                </div>
                <span class="datetime">{{ config.post.time }}</span>
            </div>
        </div>
        <p class="post-title">{{ config.post.title }}</p>
        <p class="post-content">{{ config.post.content }}</p>
        <div class="image-list" v-if="imageList.length > 0">
            <el-image class="image-item" v-for="(item, index) in imageList" :src="HandUtils.getImageUrl(item)" :key="index" lazy/>
        </div>
    </div>
</template>

<style lang="less">
    @import url("./styles/ForumPostItem.less");
</style>
<script setup lang="ts">
    import utils from '@/scripts/utils'
    import { forumPostType } from "../../types"
    import HandUtils from "@/scripts/HandUtils"
    import ForumComment from "@/forum/ForumComment.vue"

    const goBack = () => {
        onelDialogStore.setForumPostDetail(false);
    }

    onUnmounted(() => goBack())
    const onelDialogStore = utils.useOnelDialogStore();

    const config = defineProps({ post: { type: Object as PropType<forumPostType>, required: true } });
    const imageList = JSON.parse(config.post.image);
</script>

<template>
    <div class="post-detail-container">
        <el-page-header @back="goBack">
            <template #content>
                <span class="text-large font-600 mr-3">{{ config.post.title }}</span>
            </template>
        </el-page-header>
        <div class="post-body">
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

            <div class="post-content">
                <p class="post-content">{{ config.post.content }}</p>
                <div class="image-list" v-if="imageList.length > 0">
                    <el-image class="image-item" v-for="(item, index) in imageList" :src="HandUtils.getImageUrl(item)" :key="index" :preview-src-list="[HandUtils.getImageUrl(item)]" lazy/>
                </div>
            </div>
            <ForumComment :postId="config.post.pid"/>
        </div>
    </div>
</template>

<style lang="less">
    @import url("./styles/ForumPostInfo.less");
</style>
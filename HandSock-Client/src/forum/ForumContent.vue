<script setup lang="ts">
    import { Reactive } from 'vue'
    import utils from '@/scripts/utils'
    import HandUtils from '@/scripts/HandUtils'
    import RequestUtils from '@/scripts/RequestUtils'
    import PublishCenter from '@/dialog/CenterDialog/PublishCenter.vue'
    import {forumPostType} from "../../types";
    
    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const loading: Ref<boolean> = ref(true);
    const postItem: Ref<forumPostType> = ref(null);
    const onelDialogStore = utils.useOnelDialogStore();
    const postList: Reactive<Array<forumPostType>> = reactive([]);

    const getPostList = async (): Promise<void> => {
        await RequestUtils.getForumPostList(pages.value, 10).then((data) => {
            total.value = data.total;
            postList.splice(0, postList.length, ...data.items);
        }).finally(() => {
            loading.value = false;
        });
    }

    const currentChange = async (page: number): Promise<void> => {
        pages.value = page;
        await getPostList();
    }

    const openPostDetail = (post: forumPostType): void => {
        postItem.value = post
        onelDialogStore.setForumPostDetail(true);
    }
    onMounted(async (): Promise<void> => await getPostList());
</script>

<template>
    <div class="forum-content">
        <div class="post-list-container" v-loading="loading">
            <div class="post-list" v-if="postList.length > 0">
                <ForumPostItem v-for="(item, index) in postList" :key="index" :post="item" @click="openPostDetail(item)"/>
            <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange"/>
            </div>
            <div v-else style="height: 100%;">
                <el-empty description="暂无更多帖子" style="height: 100%;" />
            </div>
            <el-button class="float-button" type="primary" plain @click="HandUtils.openCustomSwalDialog(PublishCenter, {
                width: 515,
                title: '发布帖子'
            })">
                <svg t="1743928621736" class="icon" viewBox="0 0 1052 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6259" width="20" height="20"><path d="M1042.203653 913.292687a29.76645 29.76645 0 0 0-21.0668-8.529069H545.470686a29.681159 29.681159 0 0 0-29.76645 29.76645v59.703482a29.76645 29.76645 0 0 0 29.76645 29.76645h475.666167a29.76645 29.76645 0 0 0 29.76645-29.76645v-59.703482a29.76645 29.76645 0 0 0-8.529069-21.0668" fill="#ffffff" p-id="6260"></path><path d="M985.655926 303.037814L690.976599 8.529069a29.76645 29.76645 0 0 0-42.048309 0L8.651095 649.232717a29.76645 29.76645 0 0 0-8.529068 23.710811l24.478427 269.859737a29.76645 29.76645 0 0 0 26.951858 26.866567l269.689155 24.563718a29.76645 29.76645 0 0 0 23.796102-8.529069l640.533067-640.533067a29.76645 29.76645 0 0 0 0-42.048309m-695.033817 568.888889L136.587127 857.93903l-14.158254-154.120273L484.743716 341.162752l167.510911 169.131434zM736.265955 426.45344L568.755043 257.407296l101.495919-101.495918L838.444199 324.104614z" fill="#ffffff" p-id="6261"></path></svg>
            </el-button>
        </div>
        <ForumPostInfo v-if="onelDialogStore.forumPostDetail && postItem" :post="postItem"/>
    </div>
</template>

<style lang="less">
    @import url("./styles/ForumContent.less");
</style>
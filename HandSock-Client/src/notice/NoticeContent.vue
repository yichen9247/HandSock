<script setup lang="ts">
    import { Reactive } from 'vue'
    import RequestUtils from '@/scripts/RequestUtils'
    
    const loading: Ref<boolean> = ref(true);
    const postList: Reactive<Array<any>> = reactive([]);

    onMounted(async () => {
        await RequestUtils.getNoticeList().then((data) => {
            postList.splice(0, postList.length, ...data);
        }).finally(() => {
            loading.value = false;
        });
    });
</script>

<template>
    <div class="notice-content">
        <div class="post-list-container" v-loading="loading">
            <div class="post-list" v-if="postList.length > 0">
                <NoticeItem v-for="(item, index) in postList" :key="index" :post="item"/>
            </div>
            <div v-else style="height: 100%;">
                <el-empty description="暂无更多通知" style="height: 100%;" />
            </div>
        </div>
    </div>
</template>

<style lang="less">
    @import url("./styles/NoticeContent.less");
</style>
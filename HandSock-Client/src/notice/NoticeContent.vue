<script setup lang="ts">
    import { Reactive } from 'vue'
    import RequestUtils from '@/scripts/RequestUtils'

    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const loading: Ref<boolean> = ref(true);
    const noticeList: Reactive<Array<any>> = reactive([]);

    const getNoticeList = async (): Promise<void> => {
        await RequestUtils.getNoticeList(pages.value, 10).then((data) => {
            total.value = data.total;
            noticeList.splice(0, noticeList.length, ...data.items);
        }).finally(() => {
            loading.value = false;
        });
    }

    const currentChange = async (page: number): Promise<void> => {
        pages.value = page;
        await getNoticeList();
    }

    onMounted(async (): Promise<void> => await getNoticeList() );
</script>

<template>
    <div class="notice-content">
        <div class="post-list-container" v-loading="loading">
            <div class="post-list" v-if="noticeList.length > 0">
                <NoticeItem v-for="(item, index) in noticeList" :key="index" :post="item"/>
                <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange"/>
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
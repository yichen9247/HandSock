<script setup lang="ts">
    import { Reactive } from 'vue'
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import HandUtils from '@/scripts/HandUtils'
    import { arrayDataType, forumPostType, restfulType } from '../../../types'
    
    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const loading: Ref<boolean> = ref(false);
    const tableData: Reactive<Array<any>> = reactive([]);
    
    const applicationStore = utils.useApplicationStore();
    const showToast = async (type: string, message: string): Promise<void> => await utils.showToasts(type, message);
    
    onMounted(async (): Promise<void> => await getPostList());
    
    const getPostList = async (): Promise<void> => {
        loading.value = true;
        await applicationStore.socketIo.emit(socket.send.Admin.Get.GetAdminPostList, {
            page: pages.value,
            limit: 10,
        }, async (response: restfulType<arrayDataType<forumPostType>>) => {
            if (response.code !== 200) {
                await showToast('error', response.message);
            } else {
                total.value = response.data.total;
                tableData.splice(0, tableData.length, ...response.data.items);

                if (tableData.length === 0 && response.data.total > 0) {
                    pages.value = pages.value - 1;
                    await getPostList();
                }
            }
            loading.value = false;
        });
    }
    
    const currentChange = async (page: number): Promise<void> => {
        pages.value = page;
        await getPostList();
    }
    
    const sendSocketRequest = async (action: any, data: any, callback: any) => {
        await HandUtils.checkClientLoginStatus(async () => {
            await HandUtils.sendClientSocketEmit({
                data: data,
                event: action,
                callback: async (response: restfulType<any>): Promise<void> => {
                    if (response.code === 200) {
                        await getPostList();
                        await showToast('success', response.message);
                    } else await showToast('error', response.message);
                    callback && await callback(response);
                }
            });
        })
    };
    
    const handleDeletePost = (pid: string): void => {
        ElMessageBox.confirm('是否确认删除该条帖子', '删除帖子', {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
        }).then(async (): Promise<void> => await sendSocketRequest(socket.send.Admin.Del.DelAdminPost, { pid }, (): void => {}));
    };

    const getPostContent = async (content: string): Promise<void> => {
        await ElMessageBox.alert(content, '查看内容', {
            confirmButtonText: '确认',
            callback: (): void => {},
        });
    }
</script>

<template>
    <div class="content-box report-manner" v-loading="loading">
        <el-table :data="tableData" border>
            <el-table-column prop="uid" label="用户编号"/>
            <el-table-column prop="title" label="帖子标题"/>
            <el-table-column prop="type" label="图片数量" width="120">
                <template #default="scope">
                    <span>{{ JSON.parse(scope.row.image).length }}张</span>
                </template>
            </el-table-column>
            <el-table-column prop="content" label="帖子内容"/>
            <el-table-column prop="time" label="发布时间" />

            <el-table-column label="更多操作" width="200">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="getPostContent(scope.row.content)">查看内容</el-button>
                    <el-button size="small" type="danger" @click="handleDeletePost(scope.row.pid)">删除帖子</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange" v-if="tableData.length > 0"/>
    </div>
</template>
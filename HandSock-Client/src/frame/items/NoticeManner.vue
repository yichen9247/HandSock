<script setup lang="ts">
    import { Reactive } from 'vue'
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import HandUtils from '@/scripts/HandUtils'
    import { Plus, Refresh } from '@element-plus/icons-vue'
    import { adminNoticeFormType, restfulType } from '../../../types'

    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const formModel: Ref<number> = ref(0);
    const loading: Ref<boolean> = ref(false);
    const rightDrawer: Ref<boolean> = ref(false);
    const tableData: Reactive<Array<any>> = reactive([]);

    const applicationStore = utils.useApplicationStore();
    const showToast = async (type: string, message: string): Promise<void> => await utils.showToasts(type, message);

    const formData: Reactive<adminNoticeFormType> = reactive({
        nid: null, title: "", content: ""
    });

    onMounted(async () => await getNoticeList());

    const getNoticeList = async (): Promise<void> => {
        loading.value = true;
        applicationStore.socketIo.emit(socket.send.Admin.Get.GetAdminNoticeList, {
            page: pages.value,
            limit: 10,
        }, async (response: restfulType): Promise<void> => {
            if (response.code !== 200) {
                utils.showToasts('error', response.message);
            } else {
                total.value = response.data.total;
                tableData.splice(0, tableData.length, ...response.data.items);

                if (tableData.length === 0 && response.data.total > 0) {
                    pages.value = pages.value - 1;
                    await getNoticeList();
                }
            }
            loading.value = false;
        });
    }

    const currentChange = async (page: number): Promise<void> => {
        pages.value = page;
        await getNoticeList();
    }

    const createNotice = (): void => {
        formModel.value = 1;
        formData.title = "";
        formData.content = "";
        rightDrawer.value = true;
    }

    const updateNotice = (row: any): void => {
        formModel.value = 0;
        formData.nid = row.nid;
        rightDrawer.value = true;
        formData.title = row.title;
        formData.content = row.content;
    }

    const sendSocketRequest = async (action: any, data: any, callback: any): Promise<void> => {
        await HandUtils.checkClientLoginStatus(async (): Promise<void> => {
            await HandUtils.sendClientSocketEmit({
                data: data,
                event: action,
                callback: async (response: restfulType) => {
                    if (response.code !== 200) {
                        showToast('error', response.message);
                    } else {
                        await getNoticeList();
                        showToast('success', response.message);
                    }
                    callback && await callback(response);
                }
            });
        });
    };

    const handleDeleteNotice = (nid: number): void => {
        ElMessageBox.confirm(`是否确认删除编号为 ${nid} 的公告`, '删除公告', {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
        }).then(async (): Promise<void> => await sendSocketRequest(socket.send.Admin.Del.DelAdminNotice, { nid }, (): void => {}));
    };

    const refreshList = async (): Promise<void> => {
        await getNoticeList();
        await showToast('success', "刷新列表成功");
    }

    const sendCreateNotice = async (): Promise<void> => {
        if (formData.title === null || formData.content === "") return showToast('warning', "必填项为空");
        await sendSocketRequest(formModel.value ? socket.send.Admin.Add.AddAdminNotice : socket.send.Admin.Set.SetAdminNoticeInfo, formData, (): void => {});
        await drawerOnClose();
    }

    onUnmounted(() => rightDrawer.value = false);
    const drawerOnClose = async (): Promise<boolean> => rightDrawer.value = false;
</script>

<template>
    <div class="content-box notice-manner" v-loading="loading">
        <div class="button-sheet">
            <el-button type="primary" size="large" :icon="Plus" @click="createNotice">发布公告</el-button>
            <el-button type="primary" size="large" :icon="Refresh" @click="refreshList">刷新列表</el-button>
        </div>

        <el-table :data="tableData" border>
            <el-table-column prop="nid" label="公告编号" width="135" />
            <el-table-column prop="title" label="公告标题" />
            <el-table-column prop="time" label="创建时间" />
            <el-table-column prop="content" label="公告内容" />

            <el-table-column label="更多操作" width="185">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="updateNotice(scope.row)">修改公告</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteNotice(scope.row.nid)">删除公告</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange" v-if="tableData.length > 0" />
    
        <FrameDrawer :title="`${formModel ? '创建' : '修改'}公告`" :show="rightDrawer" @close="drawerOnClose">
            <el-form :model="formData" label-width="auto">
                <el-form-item label="公告标题">
                    <el-input v-model="formData.title" placeholder="请输入公告标题"/>
                </el-form-item>

                <el-form-item label="公告内容">
                    <el-input type="textarea" :rows="3" v-model="formData.content" placeholder="请输入公告内容"/>
                </el-form-item>
            </el-form>
            <el-button type="primary" size="large" style="width: 100%; margin-top: 10px;" @click="sendCreateNotice">确认{{ formModel ? "创建" : "修改" }}</el-button>
        </FrameDrawer>
    </div>
</template>
<script setup lang="ts">
    import { Reactive } from 'vue'
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import { Action } from 'element-plus'
    import { restfulType } from '../../../types'
    import { checkLoginWork, sendSocketEmit } from '@/socket/socketClient'

    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const loading: Ref<boolean> = ref(false);
    const tableData: Reactive<Array<any>> = reactive([]);

    const applicationStore = utils.useApplicationStore();
    onMounted(async (): Promise<void> => await getUploadList());
    const fileType: any = { file: '文件', avatar: '头像', images: '图片'}; 
    const showToast = async (type: string, message: string): Promise<void> => await utils.showToasts(type, message);
    

    const getUploadList = async (): Promise<void> => {
        loading.value = true;
        applicationStore.socketIo.emit(socket.send.Admin.Get.GetAdminUploadList, {
            page: pages.value,
            limit: 10,
        }, async (response: restfulType) => {
            if (response.code === 200) {
                total.value = response.data.total;
                tableData.splice(0, tableData.length, ...response.data.items);
                if (tableData.length === 0 && response.data.total > 0) {
                    pages.value = pages.value - 1;
                    await getUploadList();
                }
            } else utils.showToasts('error', response.message);
            loading.value = false
        });
    }

    const currentChange = async (page: number): Promise<void> => {
        pages.value = page;
        await getUploadList();
    }

    const sendSocketRequest = async (action: any, data: any, callback: any): Promise<void> => {
        await checkLoginWork(async () => {
            await sendSocketEmit(action, data, async (response: restfulType) => {
                if (response.code !== 200) {
                    showToast('error', response.message);
                } else {
                    await getUploadList();
                    showToast('success', response.message);
                }
                callback && await callback(response);
            });
        })
    };

    const handleDeleteUpload = (uid: string, fid: string): void => {
        ElMessageBox.alert(`是否确认删除编号为 ${uid} 的用户所上传的文件`, '删除文件', {
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonText: '确认',
            callback: async (action: Action): Promise<void> => {
                if (action === 'confirm') await sendSocketRequest(socket.send.Admin.Del.DelAdminUpload, { fid }, (): void => {});
            },
        });
    };

    const openDownload = (item: any): void => {
        if (item.type === 'images' || item.type === 'avatar') {
            utils.previewImage({ src: socket.server.config.serverUrl + (item.type === 'avatar' ? socket.server.downloadAvatar : socket.server.downloadImages) + item.path, html: `上传时间： <span style='color: var(--dominColor)'>${item.time}</span>` });
        } else
        if (item.type === 'file') open(socket.server.config.serverUrl + socket.server.downloadFile + item.path);
    }
</script>

<template>
    <div class="content-box user-manner" v-loading="loading">
        <el-table :data="tableData" border>
            <el-table-column prop="uid" label="上传用户"/>
            <el-table-column prop="time" label="上传时间" />
            <el-table-column prop="name" label="文件名称" />
            <el-table-column prop="size" label="文件大小">
                <template #default="scope">
                    <span>{{ utils.getFileSize(scope.row.size, 'MB') }}MB</span>
                </template>
            </el-table-column>
            <el-table-column prop="type" label="文件类型">
                <template #default="scope">
                    <span>{{ fileType[scope.row.type] }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="path" label="文件路径" />

            <el-table-column label="更多操作" width="185">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="openDownload(scope.row)">查看文件</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteUpload(scope.row.uid, scope.row.fid)">删除文件</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange" v-if="tableData.length > 0"/>
    </div>
</template>
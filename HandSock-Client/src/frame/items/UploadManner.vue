<!-- eslint-disable no-undef -->
 
<script setup>
    import utils from '@/scripts/utils'
    import config from '@/scripts/config'
    import { onMounted, reactive, ref } from 'vue'
    import { checkLoginWork, sendSocketEmit } from '@/socket/socketClient'

    const pages = ref(1);
    const total = ref(0);
    const loading = ref(false);
    const tableData = reactive([]);

    const applicationStore = utils.useApplicationStore();
    const showToast = (type, message) => utils.showToasts(type, message);

    onMounted(async () => await getUploadList());

    const getUploadList = async () => {
        loading.value = true;
        applicationStore.socketIo.emit("[GET:ADMIN:UPLOAD:LIST]", {
            page: pages.value,
            limit: 10,
        }, async (response) => {
            if (response.code !== 200) {
                utils.showToasts('error', response.message);
            } else {
                total.value = response.data.total;
                tableData.splice(0, tableData.length, ...response.data.items);

                if (tableData.length === 0 && response.data.total > 0) {
                    pages.value = pages.value - 1;
                    await getUploadList();
                }
            }
            loading.value = false;
        });
    }

    const currentChange = async (page) => {
        pages.value = page;
        await getUploadList();
    }

    const sendSocketRequest = async (action, data, callback) => {
        await checkLoginWork(async () => {
            await sendSocketEmit(action, data, async (response) => {
                if (response.code !== 200) {
                    showToast('error', response.message);
                } else {
                    await getUploadList();
                    showToast('success', response.message);
                }
                callback && callback(response);
            });
        })
    };

    const handleDeleteUpload = (uid, fid) => {
        ElMessageBox.alert(`是否确认删除编号为 ${uid} 的用户所上传的文件`, '删除文件', {
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonText: '确认',
            callback: (action) => {
                if (action === 'confirm') sendSocketRequest("[DEL:ADMIN:UPLOAD]", { fid }, () => {});
            },
        });
    };

    const openDownload = async (path) => {
        await checkLoginWork(() => open(config.serverAdress + '/api/upload/download/' + path));
    }
</script>

<template>
    <div class="content-box user-manner">
        <el-table :data="tableData" border v-loading="loading">
            <el-table-column prop="uid" label="上传用户"/>
            <el-table-column prop="time" label="上传时间" />
            <el-table-column prop="name" label="文件名称" />
            <el-table-column prop="path" label="文件路径" />

            <el-table-column label="更多操作" width="185">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="openDownload(scope.row.path)">下载文件</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteUpload(scope.row.uid, scope.row.fid)">删除文件</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange" v-if="tableData.length > 0"/>
    </div>
</template>
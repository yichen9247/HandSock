<script setup lang="ts">
    import { Reactive } from 'vue'
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import { Action } from 'element-plus'
    import HandUtils from '@/scripts/HandUtils'
    import { restfulType } from '../../../types'
    
    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const loading: Ref<boolean> = ref(false);
    const tableData: Reactive<Array<any>> = reactive([]);
    
    const applicationStore = utils.useApplicationStore();
    const showToast = async (type: string, message: string): Promise<void> => await utils.showToasts(type, message);
    
    onMounted(async (): Promise<void> => await getRepoList());
    
    const getRepoList = async (): Promise<void> => {
        loading.value = true;
        await applicationStore.socketIo.emit(socket.send.Admin.Get.GetAdminRepoList, {
            page: pages.value,
            limit: 10,
        }, async (response: restfulType) => {
            if (response.code !== 200) {
                showToast('error', response.message);
            } else {
                total.value = response.data.total;
                tableData.splice(0, tableData.length, ...response.data.items);

                if (tableData.length === 0 && response.data.total > 0) {
                    pages.value = pages.value - 1;
                    await getRepoList();
                }
            }
            loading.value = false;
        });
    }
    
    const currentChange = async (page: number): Promise<void> => {
        pages.value = page;
        await getRepoList();
    }
    
    const sendSocketRequest = async (action: any, data: any, callback: any) => {
        await HandUtils.checkClientLoginStatus(async () => {
            await HandUtils.sendClientSocketEmit({
                data: data,
                event: action,
                callback: async (response: restfulType): Promise<void> => {
                    if (response.code === 200) {
                        await getRepoList();
                        showToast('success', response.message);
                    } else showToast('error', response.message);
                    callback && await callback(response);
                }
            });
        })
    };
    
    const handleDeleteRepo = (rid: string): void => {
        ElMessageBox.confirm('是否确认删除该条举报', '删除举报', {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
        }).then(async (): Promise<void> => await sendSocketRequest(socket.send.Admin.Del.DelAdminRepo, { rid }, (): void => {}));
    };

    const handleDeleteChat = (sid: string): void => {
        ElMessageBox.alert(`是否确认删除该条内容`, '删除内容', {
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonText: '确认',
            callback: async (action: Action): Promise<void> => {
                if (action === 'confirm') await sendSocketRequest(socket.send.Admin.Del.DelAdminChat, { sid }, (): void => {});
            }
        });
    }

    const handleTabooUser = async (uid: string): Promise<void> => {
        ElMessageBox.alert(`是否禁言编号为 ${uid} 的用户`, '禁言用户', {
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonText: '确认',
            callback: async (action: Action): Promise<void> => {
                if (action !== 'confirm') return;
                await HandUtils.sendClientSocketEmit({
                    event: socket.send.Admin.Set.User.SetAdminUserTabooStatus,
                    data: {
                        uid: uid,
                        status: 'open'
                    },
                    callback: async (response: restfulType): Promise<void> => {
                        if (response.code !== 200) {
                            await utils.showToasts('error', response.message);
                        } else await utils.showToasts('success', response.message);
                    }
                });
            }
        });
    }

    const getChatContent = async (sid: string): Promise<void> => {
        await HandUtils.sendClientSocketEmit({
            event: socket.send.Admin.Get.GetAdminChatContent,
            data: {
                sid: sid
            },
            callback: async (response: restfulType): Promise<void> => {
                if (response.code === 200) {
                    if (response.data.type === 'file') {
                        open(socket.server.config.serverUrl + socket.server.downloadFile + response.data.content);
                    } else if (response.data.type === 'image') {
                        HandUtils.previewImageBySwal({ 
                            src: socket.server.config.serverUrl + socket.server.downloadImages + response.data.content, 
                            html: `举报时间： <span style='color: var(--dominColor)'>${response.data.time}</span>` 
                        });
                    } else ElMessageBox.alert(response.data.content, '查看内容', {
                        confirmButtonText: '确认',
                        callback: (): void => {},
                    });
                } else utils.showToasts('error', response.message);
            }
        });
    }
</script>

<template>
    <div class="content-box report-manner" v-loading="loading">
        <el-table :data="tableData" border>
            <el-table-column prop="reporterId" label="举报者"/>
            <el-table-column prop="reportedId" label="被举报者"/>
            <el-table-column prop="reason" label="举报理由" />
            <el-table-column prop="time" label="举报时间" />

            <el-table-column label="更多操作" width="320">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="getChatContent(scope.row.sid)">查看内容</el-button>
                    <el-button size="small" type="info" @click="handleTabooUser(scope.row.reportedId)">禁言</el-button>
                    <el-button size="small" type="warning" @click="handleDeleteChat(scope.row.sid)">删除内容</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteRepo(scope.row.rid)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange" v-if="tableData.length > 0"/>
    </div>
</template>
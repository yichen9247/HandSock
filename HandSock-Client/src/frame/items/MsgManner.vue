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
    const messageType: any = {
        text: '文本消息',
        file: '文件消息',
        clap: '拍拍消息',
        code: '代码消息',
        image: '图片消息',
    };

    const applicationStore = utils.useApplicationStore();
    const showToast = async (type: string, message: string): Promise<void> => await utils.showToasts(type, message);

    onMounted(async () => await getChatList());

    const getChatList = async (): Promise<void> => {
        loading.value = true;
        applicationStore.socketIo.emit(socket.send.Admin.Get.GetAdminChatList, {
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
                    await getChatList();
                }
            }
            loading.value = false;
        });
    }

    const currentChange = async (page: number): Promise<void> => {
        pages.value = page;
        await getChatList();
    }

    const openContentAlert = async (item: any): Promise<void> => {
        if (item.type === 'files') {
            open(socket.server.config.serverUrl + socket.server.downloadFile + item.content);
        } else if (item.type === 'image') {
            utils.previewImage({ src: socket.server.config.serverUrl + (item.type === 'avatar' ? socket.server.downloadAvatar : socket.server.downloadImages) + item.content, html: `上传时间： <span style='color: var(--dominColor)'>${item.time}</span>` });
        } else ElMessageBox.alert(item.content, '查看内容', {
            confirmButtonText: '确认',
            callback: () => {},
        });
    }

    const sendSocketRequest = async (action: any, data: any, callback: any): Promise<void> => {
        await checkLoginWork(async (): Promise<void> => {
            await sendSocketEmit(action, data, async (response: restfulType): Promise<void> => {
                if (response.code !== 200) {
                    showToast('error', response.message);
                } else {
                    await getChatList();
                    showToast('success', response.message);
                }
                callback && await callback(response);
            });
        })
    };

    const handleDeleteChat = (sid: string): void => {
        ElMessageBox.alert(`是否确认删除该条消息`, '删除消息', {
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonText: '确认',
            callback: async (action: Action): Promise<void> => {
                if (action === 'confirm') await sendSocketRequest(socket.send.Admin.Del.DelAdminChat, { sid }, () => {});
            }
        });
    };
</script>

<template>
    <div class="content-box message-manner" v-loading="loading">
        <el-table :data="tableData" border>
            <el-table-column prop="uid" label="用户编号"/>
            <el-table-column prop="gid" label="频道编号"/>
            <el-table-column prop="type" label="消息类型">
                <template #default="scope">
                    <span>{{ messageType[scope.row.type] }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="time" label="发送时间" />

            <el-table-column prop="deleted" label="是否撤回">
                <template #default="scope">
                    <span>{{ scope.row.deleted === 1 ? '是' : '否' }}</span>
                </template>
            </el-table-column>

            <el-table-column label="更多操作" width="185">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="openContentAlert(scope.row)">查看内容</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteChat(scope.row.sid)">删除消息</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange" v-if="tableData.length > 0"/>
    </div>
</template>
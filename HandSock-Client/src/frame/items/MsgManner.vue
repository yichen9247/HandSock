<!-- eslint-disable no-undef -->

<script setup>
    import utils from '@/scripts/utils'
    import { onMounted, reactive, ref } from 'vue'
    import { checkLoginWork, sendSocketEmit } from '@/socket/socketClient'

    const pages = ref(1);
    const total = ref(0);
    const loading = ref(false);
    const tableData = reactive([]);
    const messageType = reactive({
        text: '文本消息',
        file: '文件消息',
        clap: '拍拍消息',
        code: '代码消息',
        image: '图片消息',
    });

    const applicationStore = utils.useApplicationStore();
    const showToast = (type, message) => utils.showToasts(type, message);

    onMounted(async () => await getChatList());

    const getChatList = async () => {
        loading.value = true;
        applicationStore.socketIo.emit("[GET:ADMIN:CHAT:LIST]", {
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
                    await getChatList();
                }
            }
            loading.value = false;
        });
    }

    const currentChange = async (page) => {
        pages.value = page;
        await getChatList();
    }

    const openContentAlert = async (content) => {
        ElMessageBox.alert(content, '查看内容', {
            confirmButtonText: '确认',
            callback: () => {},
        });
    }

    const sendSocketRequest = async (action, data, callback) => {
        await checkLoginWork(async () => {
            await sendSocketEmit(action, data, async (response) => {
                if (response.code !== 200) {
                    showToast('error', response.message);
                } else {
                    await getChatList();
                    showToast('success', response.message);
                }
                callback && callback(response);
            });
        })
    };

    const handleDeleteChat = (sid) => {
        ElMessageBox.alert(`是否确认删除该条消息`, '删除消息', {
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonText: '确认',
            callback: (action) => {
                if (action === 'confirm') sendSocketRequest("[DEL:ADMIN:CHAT]", { sid }, () => {});
            },
        });
    };
</script>

<template>
    <div class="content-box message-manner">
        <el-table :data="tableData" border v-loading="loading">
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
                    <el-button size="small" type="primary" @click="openContentAlert(scope.row.content)">查看内容</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteChat(scope.row.sid)">删除消息</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange" v-if="tableData.length > 0"/>
    </div>
</template>
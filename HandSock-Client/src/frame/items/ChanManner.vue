<!-- eslint-disable no-undef -->
<script setup>
    import utils from '@/scripts/utils'
    import { Plus, Refresh } from '@element-plus/icons-vue'
    import { onMounted, onUnmounted, reactive, ref } from 'vue'
    import { checkLoginWork, sendSocketEmit } from '@/socket/socketClient'
    
    const pages = ref(1);
    const total = ref(0);
    const formModel = ref(0);
    const loading = ref(false);
    const tableData = reactive([]);
    const rightDrawer = ref(false);

    const formData = reactive({
        gid: null, name: "", avatar: "", notice: ""
    });
    
    const applicationStore = utils.useApplicationStore();
    const showToast = (type, message) => utils.showToasts(type, message);
    
    onMounted(async () => await getChanList());
    
    const getChanList = async () => {
        loading.value = true;
        await applicationStore.socketIo.emit("[GET:ADMIN:CHAN:LIST]", {
            page: pages.value,
            limit: 10,
        }, async (response) => {
            if (response.code !== 200) {
                showToast('error', response.message);
            } else {
                total.value = response.data.total;
                tableData.splice(0, tableData.length, ...response.data.items);

                if (tableData.length === 0 && response.data.total > 0) {
                    pages.value = pages.value - 1;
                    await getChanList();
                }
            }
            loading.value = false;
        });
    }
    
    const currentChange = async (page) => {
        pages.value = page;
        await getChanList();
    }
    
    const sendSocketRequest = async (action, data, callback) => {
        await checkLoginWork(async () => {
            await sendSocketEmit(action, data, async (response) => {
                if (response.code !== 200) {
                    showToast('error', response.message);
                } else {
                    await getChanList();
                    showToast('success', response.message);
                }
                callback && callback(response);
            });
        })
    };
    
    const handleDeleteChan = (gid) => {
        ElMessageBox.confirm(`是否确认删除编号为 ${gid} 的频道`, '删除频道', {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => sendSocketRequest("[DEL:ADMIN:CHAN]", { gid }));
    };
    
    const handleStatusChange = async (gid, status, actionType) => {
        ElMessageBox.confirm(`是否${status === 1 ? '隐藏' : '展示'}编号为 ${gid} 的频道`, `${status === 1 ? '隐藏' : '展示'}频道`, {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => sendSocketEmit(actionType, {
            gid,
            status: status === 1 ? 0 : 1
        }, async (response) => {
            if (response.code !== 200) {
                showToast('error', response.message);
            } else {
                await getChanList();
                showToast('success', response.message);
            }
        }));
    };
    
    const handleSetChanOpenStatus = (gid, status) => handleStatusChange(gid, status, "[SET:ADMIN:CHAN:OPEN:STATUS]");
    const handleSetChanActiveStatus = (gid, status) => handleStatusChange(gid, status, "[SET:ADMIN:CHAN:ACTIVE:STATUS]");

    const createChannel = () => {
        formModel.value = 1;
        formData.gid = null,
        formData.name = "";
        formData.avatar = "";
        formData.notice = "暂无聊天室公告";
        rightDrawer.value = true;
    }

    const updateChannel = (row) => {
        formModel.value = 0;
        formData.gid = row.gid,
        rightDrawer.value = true;
        formData.name = row.name;
        formData.avatar = row.avatar;
    }

    const refreshList = async () => {
        await getChanList();
        await showToast('success', "刷新列表成功");
    }

    const sendCreateChannel = async () => {
        if (isNaN(Number(formData.gid))) return showToast('warning', "编号格式不正确");
        if (formData.name === null || formData.avatar === "") return showToast('warning', "必填项为空");
        await sendSocketRequest(formModel.value ? "[ADD:ADMIN:CHAN]" : "[SET:ADMIN:CHAN:INFO]", formData);
        await drawerOnClose();
    }

    onUnmounted(() => rightDrawer.value = false);
    const drawerOnClose = async () => rightDrawer.value = false;
</script>

<template>
    <div class="content-box channel-manner">
        <div class="button-sheet">
            <el-button type="primary" size="large" :icon="Plus" @click="createChannel">创建频道</el-button>
            <el-button type="primary" size="large" :icon="Refresh" @click="refreshList">刷新列表</el-button>
        </div>

        <el-table :data="tableData" border v-loading="loading">
            <el-table-column prop="gid" label="频道编号"/>
            <el-table-column prop="name" label="频道名称"/>
            <el-table-column prop="avatar" label="频道头像" />

            <el-table-column prop="deleted" label="是否开放">
                <template #default="scope">
                    <span>{{ scope.row.open === 1 ? '是' : '否' }}</span>
                </template>
            </el-table-column>
            
            <el-table-column prop="deleted" label="首页展示">
                <template #default="scope">
                    <span>{{ scope.row.active === 1 ? '是' : '否' }}</span>
                </template>
            </el-table-column>

            <el-table-column prop="notice" label="频道公告" />

            <el-table-column label="更多操作" width="260">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="updateChannel(scope.row)">修改</el-button>
                    <el-button size="small" type="warning" @click="handleSetChanActiveStatus(scope.row.gid, scope.row.active)">{{ scope.row.active === 1 ? '隐藏' : '展示' }}</el-button>
                    <el-button size="small" type="info" @click="handleSetChanOpenStatus(scope.row.gid, scope.row.open)">{{ scope.row.open === 1 ? '关闭' : '开启' }}</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteChan(scope.row.gid)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange" v-if="tableData.length > 0"/>
    
        
        <FrameDrawer :title="`${formModel ? '创建' : '修改'}频道`" :show="rightDrawer" @close="drawerOnClose">
            <el-form :model="formData" label-width="auto">
                <el-form-item label="频道编号" v-if="formModel">
                    <el-input v-model="formData.gid" placeholder="请输入频道编号"/>
                </el-form-item>

                <el-form-item label="频道名称">
                    <el-input v-model="formData.name" placeholder="请输入频道名称"/>
                </el-form-item>

                <el-form-item label="频道头像">
                    <el-input v-model="formData.avatar" placeholder="请输入频道头像（链接）"/>
                </el-form-item>

                <el-form-item label="频道公告">
                    <el-input type="textarea" v-model="formData.notice" :rows="2" placeholder="请输入频道公告（可以为空）"/>
                </el-form-item>
            </el-form>
            <el-button type="primary" size="large" style="width: 100%; margin-top: 10px;" @click="sendCreateChannel">确认{{ formModel ? "创建" : "修改" }}</el-button>
        </FrameDrawer>
    </div>
</template>
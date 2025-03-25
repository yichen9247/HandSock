<script setup lang="ts">
    import { Reactive } from 'vue'
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import HandUtils from '@/scripts/HandUtils'
    import { Plus, Refresh } from '@element-plus/icons-vue'
    import { adminChanFormType, restfulType } from '../../../types'
    
    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const formModel: Ref<number> = ref(0);
    const loading: Ref<boolean> = ref(false);
    const rightDrawer: Ref<boolean> = ref(false);
    const tableData: Reactive<Array<any>> = reactive([]);

    const formData: Reactive<adminChanFormType> = reactive({
        gid: null, name: "", avatar: "", notice: "", aiRole: false, aiContent: ""
    });
    
    const applicationStore = utils.useApplicationStore();
    const showToast = async (type: string, message: string): Promise<void> => await utils.showToasts(type, message);
    
    onMounted(async (): Promise<void> => await getChanList());
    
    const getChanList = async (): Promise<void> => {
        loading.value = true;
        await applicationStore.socketIo.emit(socket.send.Admin.Get.GetAdminChanList, {
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
                    await getChanList();
                }
            }
            loading.value = false;
        });
    }
    
    const currentChange = async (page: number): Promise<void> => {
        pages.value = page;
        await getChanList();
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
                        await getChanList();
                        showToast('success', response.message);
                    }
                    callback && await callback(response);
                }
            });
        });
    };
    
    const handleDeleteChan = (gid: number): void => {
        ElMessageBox.confirm(`是否确认删除编号为 ${gid} 的频道`, '删除频道', {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
        }).then(async (): Promise<void> => await sendSocketRequest(socket.send.Admin.Del.DelAdminChan, { gid }, (): void => {}));
    };
    
    const handleStatusChange = async (gid: number, status: number, actionType: string) => {
        if (actionType === "[SET:ADMIN:CHAN:OPEN:STATUS]") {
            ElMessageBox.confirm(`是否${status === 1 ? '关闭' : '开启'}编号为 ${gid} 的频道`, `${status === 1 ? '关闭' : '开启'}频道`, {
                confirmButtonText: '确认',
                cancelButtonText: '取消',
            }).then(async (): Promise<void> => await HandUtils.sendClientSocketEmit({
                event: actionType,
                data: {
                    gid,
                    status: status === 1 ? 0 : 1
                },
                callback: async (response: restfulType): Promise<void> => {
                    if (response.code !== 200) {
                        showToast('error', response.message);
                    } else {
                        await getChanList();
                        showToast('success', response.message);
                    }
                }
            })
        )} else if (actionType === "[SET:ADMIN:CHAN:ACTIVE:STATUS]") {
            ElMessageBox.confirm(`是否${status === 1 ? '隐藏' : '展示'}编号为 ${gid} 的频道`, `${status === 1 ? '隐藏' : '展示'}频道`, {
                confirmButtonText: '确认',
                cancelButtonText: '取消',
            }).then(async (): Promise<void> => await HandUtils.sendClientSocketEmit({
                event: actionType,
                data: {
                    gid,
                    status: status === 1 ? 0 : 1
                },
                callback: async (response: restfulType): Promise<void> => {
                    if (response.code !== 200) {
                        showToast('error', response.message);
                    } else {
                        await getChanList();
                        showToast('success', response.message);
                    }
                }
            })
        )}
    };
    
    const handleSetChanOpenStatus = async (gid: number, status: number): Promise<void> => await handleStatusChange(gid, status, "[SET:ADMIN:CHAN:OPEN:STATUS]");
    const handleSetChanActiveStatus = async (gid: number, status: number): Promise<void> => await handleStatusChange(gid, status, "[SET:ADMIN:CHAN:ACTIVE:STATUS]");

    const createChannel = (): void => {
        formModel.value = 1;
        formData.gid = null,
        formData.name = "";
        formData.avatar = "";
        formData.aiRole = false;
        formData.notice = "暂无聊天室公告";
        rightDrawer.value = true;
    }

    const updateChannel = (row: any): void => {
        formModel.value = 0;
        formData.gid = row.gid,
        rightDrawer.value = true;
        formData.name = row.name;
        formData.notice = row.notice;
        formData.avatar = row.avatar;
        formData.aiRole = row.aiRole === 1;
    }

    const refreshList = async (): Promise<void> => {
        await getChanList();
        await showToast('success', "刷新列表成功");
    }

    const sendCreateChannel = async (): Promise<void> => {
        if (isNaN(Number(formData.gid))) return showToast('warning', "编号格式不正确");
        if (formData.name === null || formData.avatar === "") return showToast('warning', "必填项为空");
        await sendSocketRequest(formModel.value ? socket.send.Admin.Add.AddAdminChan : socket.send.Admin.Set.SetAdminChanInfo, formData, (): void => {});
        await drawerOnClose();
    }

    onUnmounted(() => rightDrawer.value = false);
    const drawerOnClose = async (): Promise<boolean> => rightDrawer.value = false;
</script>

<template>
    <div class="content-box channel-manner" v-loading="loading">
        <div class="button-sheet">
            <el-button type="primary" size="large" :icon="Plus" @click="createChannel">创建频道</el-button>
            <el-button type="primary" size="large" :icon="Refresh" @click="refreshList">刷新列表</el-button>
        </div>

        <el-table :data="tableData" border>
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

            <el-table-column prop="deleted" label="AI频道">
                <template #default="scope">
                    <span>{{ scope.row.aiRole === 1 ? '是' : '否' }}</span>
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
                <el-checkbox v-model="formData.aiRole">设置为AI频道</el-checkbox>
            </el-form>
            <el-button type="primary" size="large" style="width: 100%; margin-top: 10px;" @click="sendCreateChannel">确认{{ formModel ? "创建" : "修改" }}</el-button>
        </FrameDrawer>
    </div>
</template>
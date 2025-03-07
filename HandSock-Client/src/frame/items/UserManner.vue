<script setup lang="ts">
    import { Reactive } from 'vue'
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import { Action } from 'element-plus'
    import HandUtils from '@/scripts/HandUtils'
    import { restfulType, adminUserFormType } from '../../../types'

    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const formModel: Ref<number> = ref(0);
    const loading: Ref<boolean> = ref(false);
    const rightDrawer: Ref<boolean> = ref(false);
    const tableData: Reactive<Array<any>> = reactive([]);

    const formData: Reactive<adminUserFormType> = reactive({
        uid: null, nick: "", username: "", avatar: "", robot: false
    });

    const applicationStore = utils.useApplicationStore();
    const showToast = async (type: string, message: string): Promise<void> => await utils.showToasts(type, message);

    onMounted(async (): Promise<void> => await getUserList());

    const getUserList = async (): Promise<void> => {
        loading.value = true;
        applicationStore.socketIo.emit(socket.send.Admin.Get.GetAdminUserList, {
            page: pages.value,
            limit: 10,
        }, async (response: restfulType) => {
            if (response.code !== 200) {
                utils.showToasts('error', response.message);
            } else {
                total.value = response.data.total;
                tableData.splice(0, tableData.length, ...response.data.items);

                if (tableData.length === 0 && response.data.total > 0) {
                    pages.value = pages.value - 1;
                    await getUserList();
                }
            }
            loading.value = false;
        });
    }

    const currentChange = async (page: number): Promise<void> => {
        pages.value = page;
        await getUserList();
    }

    const sendSocketRequest = async (action: any, data: any, callback: any) => {
        await HandUtils.checkClientLoginStatus(async (): Promise<void> => {
            await HandUtils.sendClientSocketEmit({
                data: data,
                event: action,
                callback: async (response: restfulType) => {
                    if (response.code === 200) {
                        await getUserList();
                        showToast('success', response.message);
                    } else showToast('error', response.message);
                    callback && await callback(response);
                }
            });
        });
    };

    const handleDeleteUser = (uid: string): void => {
        ElMessageBox.alert(`是否确认删除编号为 ${uid} 的用户`, '删除用户', {
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonText: '确认',
            callback: async (action: any): Promise<void> => {
                if (action === 'confirm') await sendSocketRequest(socket.send.Admin.Del.DelAdminUser, { uid }, () => {});
            },
        });
    };

    const validateInput = async (password: string): Promise<boolean> => {
        const textRegex = "^[a-zA-Z0-9]+$";
        if (!password.match(textRegex)) {
            await utils.showToasts('error', '密码格式不合规');
            return false;
        }
        if (password.length < 5 || password.length > 20) {
            await utils.showToasts('error', '密码长度不合规');
            return false;
        }
        return true;
    };

    const handleUpdateUserPassword = (uid: string): void => {
        ElMessageBox.prompt('请输入要修改的密码：', '修改密码', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
        }).then(async ({ value }): Promise<void> => {
            if (value === '' || value === null) return showToast('error', '输入格式不合规！');
            if (!validateInput(value)) return;
            await sendSocketRequest(socket.send.Admin.Set.User.SetAdminUserPassword, { uid, password: value }, (): void => {});
        }).catch((): void => {});
    };

    const handleUpdateUserTabooStatus = async (uid: string, status: string): Promise<void> => {
        ElMessageBox.alert(`是否${status === 'open' ? '解禁' : '禁言'}编号为 ${uid} 的用户`, `${status === 'open' ? '解禁' : '禁言'}用户`, {
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonText: '确认',
            callback: async (action: Action) => {
                if (action !== 'confirm') return;
                await HandUtils.sendClientSocketEmit({
                    event: socket.send.Admin.Set.User.SetAdminUserTabooStatus,
                    data: {
                        uid: uid,
                        status: status === 'open' ? 'close' : 'open'
                    },
                    callback: async (response: restfulType): Promise<void> => {
                        if (response.code !== 200) {
                            await utils.showToasts('error', response.message);
                        } else {
                            await getUserList();
                            await utils.showToasts('success', response.message);   
                        }
                    }
                });
            }
        });
    }

    
    const updateUserInfo = async (row: any): Promise<void> => {
        formModel.value = 0;
        formData.uid = row.uid;
        formData.nick = row.nick;
        rightDrawer.value = true;
        formData.avatar = row.avatar;
        formData.robot = row.isRobot == 1;
        formData.username = row.username;
    }

    const sendUpdateUserInfo = async (): Promise<void> => {
        if (formData.username === null || formData.avatar === "" || formData.nick === "") return showToast('warning', "必填项为空");
        await sendSocketRequest(socket.send.Admin.Set.User.SetAdminUserInfo, formData, (): void => {});
        drawerOnClose();
    }

    onUnmounted(() => rightDrawer.value = false);
    const drawerOnClose = (): boolean => rightDrawer.value = false;
</script>

<template>
    <div class="content-box user-manner" v-loading="loading">
        <el-table :data="tableData" border>
            <el-table-column prop="uid" label="用户编号"/>
            <el-table-column prop="nick" label="用户昵称"/>
            <el-table-column prop="avatar" label="用户头像" />
            <el-table-column prop="username" label="用户名称" />
            <el-table-column prop="regTime" label="注册时间" />

            <el-table-column label="更多操作" width="260">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="updateUserInfo(scope.row)">编辑</el-button>
                    <el-button size="small" type="warning" @click="handleUpdateUserPassword(scope.row.uid)">改密</el-button>
                    <el-button size="small" type="info" @click="handleUpdateUserTabooStatus(scope.row.uid, scope.row.taboo)">{{ scope.row.taboo === 'open' ? '解禁' : '禁言' }}</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteUser(scope.row.uid)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange" v-if="tableData.length > 0"/>
    
        <FrameDrawer :title="`${formModel ? '新增' : '编辑'}用户`" :show="rightDrawer" @close="drawerOnClose">
            <el-form :model="formData" label-width="auto">
                <el-form-item label="用户账号">
                    <el-input v-model="formData.username" placeholder="请输入用户账户"/>
                </el-form-item>

                <el-form-item label="用户昵称">
                    <el-input v-model="formData.nick" placeholder="请输入用户昵称"/>
                </el-form-item>

                <el-form-item label="用户头像">
                    <el-input v-model="formData.avatar" placeholder="请输入用户头像"/>
                </el-form-item>
                <el-checkbox v-model="formData.robot">设置为机器人</el-checkbox>
            </el-form>
            <el-button type="primary" size="large" style="width: 100%; margin-top: 10px;" @click="sendUpdateUserInfo">确认{{ formModel ? "创建" : "修改" }}</el-button>
        </FrameDrawer>
    </div>
</template>
<!-- eslint-disable no-undef -->

<script setup>
    import utils from '@/scripts/utils'
    import { onMounted, onUnmounted, reactive, ref } from 'vue'
    import { checkLoginWork, sendSocketEmit } from '@/socket/socketClient'

    const pages = ref(1);
    const total = ref(0);
    const formModel = ref(0);
    const loading = ref(false);
    const tableData = reactive([]);
    const rightDrawer = ref(false);

    const formData = reactive({
        uid: null, username: "", avatar: "", notice: "", robot: false
    });

    const applicationStore = utils.useApplicationStore();
    const showToast = (type, message) => utils.showToasts(type, message);

    onMounted(async () => await getUserList());

    const getUserList = async () => {
        loading.value = true;
        applicationStore.socketIo.emit("[GET:ADMIN:USER:LIST]", {
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
                    await getUserList();
                }
            }
            loading.value = false;
        });
    }

    const currentChange = async (page) => {
        pages.value = page;
        await getUserList();
    }

    const sendSocketRequest = async (action, data, callback) => {
        await checkLoginWork(async () => {
            await sendSocketEmit(action, data, async (response) => {
                if (response.code !== 200) {
                    showToast('error', response.message);
                } else {
                    await getUserList();
                    showToast('success', response.message);
                }
                callback && callback(response);
            });
        })
    };

    const handleDeleteUser = (uid) => {
        ElMessageBox.alert(`是否确认删除编号为 ${uid} 的用户`, '删除用户', {
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonText: '确认',
            callback: (action) => {
                if (action === 'confirm') sendSocketRequest("[DEL:ADMIN:USER]", { uid }, () => {});
            },
        });
    };

    const validateInput = (password) => {
        const textRegex = "^[a-zA-Z0-9]+$";
        if (!password.match(textRegex)) {
            utils.showToasts('error', '密码格式不合规');
            return false;
        }
        if (password.length < 5 || password.length > 20) {
            utils.showToasts('error', '密码长度不合规');
            return false;
        }
        return true;
    };

    const handleUpdateUserPassword = (uid) => {
        ElMessageBox.prompt('请输入要修改的密码：', '修改密码', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
        }).then(({ value }) => {
            if (value === '' || value === null) return showToast('error', '输入格式不合规！');
            if (!validateInput(value)) return;
            sendSocketRequest("[SET:ADMIN:USER:PASSWORD]", { uid, password: value }, () => {});
        }).catch(() => {});
    };

    const handleUpdateUserTabooStatus = async (uid, status) => {
        ElMessageBox.alert(`是否${status === 'open' ? '解禁' : '禁言'}编号为 ${uid} 的用户`, `${status === 'open' ? '解禁' : '禁言'}用户`, {
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonText: '确认',
            callback: async (action) => {
                if (action !== 'confirm') return;
                await sendSocketEmit("[SET:ADMIN:USER:TABOO:STATUS]", {
                    uid: uid,
                    status: status === 'open' ? 'close' : 'open'
                }, async (response) => {
                    if (response.code !== 200) {
                        utils.showToasts('error', response.message);
                    } else {
                        await getUserList();
                        utils.showToasts('success', response.message);   
                    }
                });
            },
        });
    }

    
    const updateUserInfo = async (row) => {
        formModel.value = 0;
        formData.uid = row.uid;
        formData.nick = row.nick;
        rightDrawer.value = true;
        formData.avatar = row.avatar;
        formData.robot = row.isRobot == 1;
        formData.username = row.username;
    }

    const sendUpdateUserInfo = async () => {
        if (formData.username === null || formData.avatar === "" || formData.nick === "") return showToast('warning', "必填项为空");
        await sendSocketRequest("[SET:ADMIN:USER:INFO]", formData);
        await drawerOnClose();
    }

    onUnmounted(() => rightDrawer.value = false);
    const drawerOnClose = async () => rightDrawer.value = false;
</script>

<template>
    <div class="content-box user-manner">
        <el-table :data="tableData" border v-loading="loading">
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
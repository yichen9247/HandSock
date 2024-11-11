<!-- eslint-disable no-undef -->

<script setup>
    import utils from '@/scripts/utils'
    import { onMounted, reactive } from 'vue';
    import { checkLoginWork, sendSocketEmit } from '@/socket/socketClient'

    const systemConfig = reactive({
        taboo: false,
        upload: false
    });

    const clientBoard = reactive([
        { key: "是否确认强制重连除了管理员之外的所有在线用户的连接", name: "强制刷新用户连接" },
        { key: "是否确认强制刷新除了管理员之外的所有在线用户的页面", name: "强制刷新用户前端" },
        { key: "是否确认清空所有聊天记录", name: "清理所有聊天记录" }
    ]);

    onMounted(async () => {
        await checkLoginWork(() => {
            sendSocketEmit("[GET:SYSTEM:CONFIG]", null, (response) => {
                if (response.code !== 200) {
                    utils.showToasts('error', response.message);   
                } else {
                    systemConfig.taboo = response.data.find(item => item.name === 'taboo').value === 'open';
                    systemConfig.upload = response.data.find(item => item.name === 'upload').value === 'open';
                }
            });
        });
    });

    const setSystemTaboo = async () => {
        await checkLoginWork(() => {
            sendSocketEmit("[SET:SYSTEM:CONFIG:TABOO]", {
                status: systemConfig.taboo ? 0 : 1
            }, (response) => {
                if (response.code !== 200) {
                    utils.showToasts('error', response.message);   
                } else {
                    utils.showToasts('success', response.message);   
                    systemConfig.taboo = response.data.status === 'open';
                }
            });
        });
    }

    const setSystemUpload = async () => {
        await checkLoginWork(() => {
            sendSocketEmit("[SET:SYSTEM:CONFIG:UPLOAD]", {
                status: systemConfig.upload ? 0 : 1
            }, (response) => {
                if (response.code !== 200) {
                    utils.showToasts('error', response.message);   
                } else {
                    utils.showToasts('success', response.message);   
                    systemConfig.upload = response.data.status === 'open';
                }
            });
        });
    }

    const adminSystemSender = async (event, index) => {
        await checkLoginWork(async () => {
            ElMessageBox.alert(clientBoard[index].key, clientBoard[index].name, {
                showCancelButton: true,
                cancelButtonText: '取消',
                confirmButtonText: '确认',
                callback: async (action) => {
                    if (action === 'confirm') {
                        await sendSocketEmit(event, null, async (response) => {
                            if (response.code !== 200) {
                                showToast('error', response.message);
                            } else showToast('success', response.message);
                        })
                    }
                },
            });
        });
    }
</script>

<template>
    <div class="admin-page">
        <el-button type="primary" @click="adminSystemSender('[RE:FORCE:CONNECT]', 0)">强制刷新用户连接（软重连）</el-button>
        <el-button type="primary" @click="adminSystemSender('[RE:FORCE:LOAD]', 1)">强制刷新用户前端（硬重连）</el-button>
        <el-button type="primary" @click="adminSystemSender('[RE:HISTORY:CLEAR]', 2)">清理所有聊天记录</el-button>
        <el-button type="primary" @click="setSystemTaboo">{{ systemConfig.taboo ? '关闭全频禁言' : '开启全频禁言' }}</el-button>
        <el-button type="primary" @click="setSystemUpload">{{ systemConfig.upload ? '关闭文件上传' : '开启文件上传' }}</el-button>
    </div>
</template>

<style lang="less">
    @import url("./styles/AdminPage.css");

    div.admin-page {
        padding-top: 5px;

        button.el-button {
            margin-left: unset;
            margin-right: 15px;
            margin-bottom: 20px;
        }
    }
</style>
<script setup lang="ts">
    import { Reactive } from 'vue'
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import HandUtils from '@/scripts/HandUtils'
    import { restfulType, adminSystemMannerType } from '../../../types'

    const systemConfig: Reactive<adminSystemMannerType> = reactive({
        taboo: false,
        upload: false,
        playlist: "",
        register: false,
        version: "",
        download: ""
    });
    const loading: Ref<boolean> = ref(false);
    const applicationStore = utils.useApplicationStore();
    const showToast = (type: string, message: string) => utils.showToasts(type, message);

    const clientBoard: Reactive<any> = reactive([
        { key: "是否确认强制重连除了管理员之外的所有在线用户的连接", name: "强制刷新用户连接" },
        { key: "是否确认强制刷新除了管理员之外的所有在线用户的页面", name: "强制刷新用户前端" },
        { key: "是否确认清空所有聊天记录", name: "清理所有聊天记录" }
    ]);

    onMounted(async (): Promise<void> => {
        applicationStore.socketIo.emit(socket.send.Admin.Get.GetAdminSystemConfig, null, (response: restfulType) => {
            if (response.code === 200) {
                systemConfig.playlist = response.data.find((item: { name: string }) => item.name === 'playlist').value;
                systemConfig.taboo = response.data.find((item: { name: string }) => item.name === 'taboo').value === 'open';
                systemConfig.upload = response.data.find((item: { name: string }) => item.name === 'upload').value === 'open';
                systemConfig.register = response.data.find((item: { name: string }) => item.name === 'register').value === 'open';
                systemConfig.version = response.data.find((item: { name: string }) => item.name === 'version').value;
                systemConfig.download = response.data.find((item: { name: string }) => item.name === 'download').value;
            } else utils.showToasts('error', response.message);
        });
        loading.value = false;
    });

    const setSystemTaboo = async (): Promise<void> => {
        await HandUtils.checkClientLoginStatus(async () => {
            await HandUtils.sendClientSocketEmit({
                event: socket.send.Set.SetSystemTabooStatus,
                data: {
                    value: systemConfig.taboo ? "close" : "open"
                },
                callback: (response: restfulType): void => {
                    if (response.code === 200) {
                        utils.showToasts('success', response.message);
                        systemConfig.taboo = response.data.status === 'open';
                    } else utils.showToasts('error', response.message);
                }
            });
        });
    }

    const setSystemUpload = async (): Promise<void> => {
        await HandUtils.checkClientLoginStatus(async () => {
            await HandUtils.sendClientSocketEmit({
                event: socket.send.Admin.Set.System.SetSystenConfigUpload,
                data: {
                    value: systemConfig.upload ? "close" : "open"
                },
                callback: (response: restfulType): void => {
                    if (response.code !== 200) {
                        utils.showToasts('error', response.message);   
                    } else {
                        utils.showToasts('success', response.message);   
                        systemConfig.upload = response.data.status === 'open';
                    }
                }
            });
        });
    }

    const setSystemRegister = async (): Promise<void> => {
        await HandUtils.checkClientLoginStatus(async () => {
            await HandUtils.sendClientSocketEmit({
                event: socket.send.Admin.Set.System.SetSystemConfigRegister,
                data: {
                    value: systemConfig.register ? "close" : "open"
                },
                callback: (response: restfulType): void => {
                    if (response.code === 200) {
                        utils.showToasts('success', response.message);
                        systemConfig.register = response.data.status === 'open';
                    } else utils.showToasts('error', response.message);
                }
            });
        });
    }

    const adminSystemSender = async (event: string, index: number): Promise<void> => {
        await HandUtils.checkClientLoginStatus(async (): Promise<void> => {
            ElMessageBox.alert(clientBoard[index].key, clientBoard[index].name, {
                showCancelButton: true,
                cancelButtonText: '取消',
                confirmButtonText: '确认',
                callback: async (action: any): Promise<void> => {
                    if (action === 'confirm') {
                        await HandUtils.sendClientSocketEmit({
                            data: null,
                            event: event,
                            callback: async (response: restfulType) => {
                                if (response.code !== 200) {
                                    showToast('error', response.message);
                                } else showToast('success', response.message);
                            }
                        });
                    }
                }
            });
        });
    }

    const setSystemConfigValue = async ({ name, value }): Promise<void> => {
        await HandUtils.checkClientLoginStatus(async () => {
            await HandUtils.sendClientSocketEmit({
                event: socket.send.Admin.Set.System.SetSystemConfigValue,
                data: { name, value },
                callback: (response: restfulType): void => {
                    if (response.code !== 200) {
                        utils.showToasts('error', response.message);
                        systemConfig.playlist = response.data.status;
                    } else utils.showToasts('success', response.message);
                }
            });
        });
    }
</script>

<template>
    <div class="content-box system-manner" v-loading="loading">
        <p class="set-title">背景音乐</p>
        <div class="flex-line">
            <el-input v-model="systemConfig.playlist" placeholder="请输入网易云歌单ID" clearable/>
            <el-button type="primary" @click="setSystemConfigValue({
                name: 'playlist',
                value: systemConfig.playlist
            })">应用设置</el-button>
        </div>

        <p class="set-title">软件设置</p>
        <div class="flex-line">
            <el-input v-model="systemConfig.version" placeholder="请输入版本名称" clearable/>
            <el-button type="primary" @click="setSystemConfigValue({
                name: 'version',
                value: systemConfig.version
            })">设置最新版本</el-button>

            <el-input v-model="systemConfig.download" placeholder="请输入下载链接" clearable/>
            <el-button type="primary" @click="setSystemConfigValue({
                name: 'download',
                value: systemConfig.download
            })">设置下载链接</el-button>
        </div>

        <p class="set-title">其它设置</p>
        <el-button type="primary" size="large" @click="adminSystemSender('[RE:FORCE:CONNECT]', 0)">强制刷新用户连接（软重连）</el-button>
        <el-button type="primary" size="large" @click="adminSystemSender('[RE:FORCE:LOAD]', 1)">强制刷新用户前端（硬重连）</el-button>
        <el-button type="primary" size="large" @click="adminSystemSender('[RE:HISTORY:CLEAR]', 2)">清理聊天记录</el-button>
        <el-button type="primary" size="large" @click="setSystemTaboo">{{ systemConfig.taboo ? '关闭全频禁言' : '开启全频禁言' }}</el-button>
        <el-button type="primary" size="large" @click="setSystemUpload">{{ systemConfig.upload ? '关闭文件上传' : '开启文件上传' }}</el-button>
        <el-button type="primary" size="large" @click="setSystemRegister">{{ systemConfig.register ? '关闭用户注册' : '开启用户注册' }}</el-button>
    </div>
</template>

<style lang="less">
    div.system-manner {
        button.el-button {
            margin-left: unset;
            margin-right: 20px;
            margin-bottom: 20px;
        }

        p.set-title {
            font-size: 16px;
            margin-bottom: 20px;
        }

        div.flex-line {
            height: unset;
            display: flex;
            justify-content: unset;

            div.el-input {
                height: 38px;
                width: 200px;
            }

            button.el-button {
                height: 38px;
                min-width: 100px;
                margin-left: 15px;
            }
        }
    }
</style>
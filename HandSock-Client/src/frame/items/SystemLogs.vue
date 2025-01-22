<script setup lang="ts">
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import { Action } from 'element-plus'
    import { restfulType } from '../../../types'
    import { Delete } from '@element-plus/icons-vue'
    import { sendSocketEmit } from '@/socket/socketClient'
    import { ref, onMounted, reactive, onUnmounted, Reactive } from 'vue'

    const logsLimit = 200;
    const logLine: Ref<number> = ref(0);
    const loading: Ref<boolean> = ref(true);
    const logList: Reactive<any> = reactive([]);
    const systemLogs: Reactive<any> = reactive([]);
    const isScrollBottom: Ref<boolean> = ref(false);
    const applicationStore = utils.useApplicationStore();
    const showToast = async (type: string, message: string): Promise<void> => await utils.showToasts(type, message);

    onMounted((): void => getSystemLogsList());

    const getSystemLogsList = (): void => {
        applicationStore.socketIo.emit(socket.send.Admin.Get.GetAdminSystemLogs, null, (response: restfulType): void => {
            if (response.code === 200) {
                logList.length = 0;
                systemLogs.length = 0;
                logList.splice(0, logList.length, ...response.data.split('\n'));
                setTimeout(() => {
                    onLogsLoad();
                }, 500);
            } else utils.showToasts('error', response.message);
            setTimeout((): boolean => loading.value = false, 500);
        });
    }

    const onLogsLoad = () => {
        logLine.value += logsLimit;
        systemLogs.splice(0, systemLogs.length, ...logList.splice(0, logLine.value));
    }

    const handleScroll = (event: Event) => {
        const { target } = event;
        if (target instanceof HTMLElement) {
            const { scrollTop, scrollHeight, clientHeight } = target;
            isScrollBottom.value = scrollTop + clientHeight >= scrollHeight - 5;
            if (isScrollBottom.value) onLogsLoad();
        }
    };

    onUnmounted(() => {
        const list = document.querySelector('.infinite-list');
        if (list) list.removeEventListener('scroll', handleScroll);
    });

    const deleteSystemLogs = (): void => {
        ElMessageBox.alert(`是否确认删除所有系统日志`, '清空日志', {
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonText: '确认',
            callback: async (action: Action): Promise<void> => {
                if (action === 'confirm') await sendSocketEmit(socket.send.Admin.Del.DELAdminSystemLogs, null, (response: restfulType): void => {
                    if (response.code === 200) {
                        getSystemLogsList();
                        loading.value = true;
                        logLine.value = logsLimit;
                        showToast('success', response.message);
                    } else utils.showToasts('error', response.message);
                });
            }
        });
    }
</script>

<template>
    <div class="content-box system-logs" v-loading="loading" element-loading-background="rgba(51, 51, 51, 1)">
        <div class="system-logs-container">
            <el-empty description="暂无更多日志" v-if="(systemLogs.length === 1 || systemLogs.length === 0) && systemLogs[0] === ''" style="margin: auto;"/>
            <div class="container" v-else>
                <div class="systemLogs-list">
                    <p class="systemLogs-list-item" v-for="(item, index) in systemLogs" :key="index">{{ item }}</p>
                </div>
                <div class="logs-btn">
                    <el-button v-if="!loading" size="large" class="btn" type="primary" :icon="Delete" circle @click="deleteSystemLogs()"/>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="less">
    @import url(./styles/SystemLogs.less);
</style>
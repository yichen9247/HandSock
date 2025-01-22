<script setup lang="ts">
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import { checkLoginWork } from '@/socket/socketClient'

    const applicationStore = utils.useApplicationStore();
    
    const chatMessage = defineProps({
        message: {
            type: Object,
            required: true
        }
    });
    
    /**
     * 处理文件下载
     * @param {string} path - 文件路径
     */
    const openDownload = async (path: string): Promise<void> => {
        const downloadUrl = socket.server.config.serverUrl + socket.server.downloadFile + path;
        await checkLoginWork((): Window => open(downloadUrl));
    }
</script>

<template>
    <div class="file-message" :style="{ border: applicationStore.isDeviceMobile ? 'none' : '1px solid rgba(60, 60, 67, 0.5)' }">
        <div class="file-lazy" @click="openDownload(chatMessage.message.content)">
            <div class="icon-box">
                <svg t="1737351489294" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15878" width="36" height="36"><path d="M783.1552 910.7968H263.5264c-44.1856 0-80.0256-35.84-80.0256-80.0256v-137.2672H863.232v137.2672c0 44.1856-35.84 80.0256-80.0768 80.0256z" fill="#ffa115" p-id="15879"></path><path d="M760.832 948.9408H273.408c-73.4208 0-133.12-59.6992-133.12-133.12V201.0112c0-73.4208 59.6992-133.12 133.12-133.12h487.3728c73.4208 0 133.12 59.6992 133.12 133.12v614.8096c0.0512 73.4208-59.6992 133.12-133.0688 133.12zM273.408 129.3312c-39.5264 0-71.68 32.1536-71.68 71.68v614.8096c0 39.5264 32.1536 71.68 71.68 71.68h487.3728c39.5264 0 71.68-32.1536 71.68-71.68V201.0112c0-39.5264-32.1536-71.68-71.68-71.68H273.408z" fill="#474A54" p-id="15880"></path><path d="M531.4048 392.0896h-196.096c-16.9472 0-30.72-13.7728-30.72-30.72s13.7728-30.72 30.72-30.72h196.096c16.9472 0 30.72 13.7728 30.72 30.72s-13.7728 30.72-30.72 30.72zM698.0608 560.6912H335.3088c-16.9472 0-30.72-13.7728-30.72-30.72s13.7728-30.72 30.72-30.72h362.752c16.9472 0 30.72 13.7728 30.72 30.72s-13.7728 30.72-30.72 30.72zM698.0608 724.224H335.3088c-16.9472 0-30.72-13.7728-30.72-30.72s13.7728-30.72 30.72-30.72h362.752c16.9472 0 30.72 13.7728 30.72 30.72s-13.7728 30.72-30.72 30.72z" fill="#474A54" p-id="15881"></path></svg>
            </div>
            <div class="content-box">分享了文件</div>
        </div>
    </div>
</template>

<style lang="less">
    @import url("./styles/ChatFileMessage.less");
</style>
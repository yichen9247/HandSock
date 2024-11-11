<script setup>
    import config from '@/scripts/config'
    import { checkLoginWork } from '@/socket/socketClient'

    const chatMessage = defineProps({ message: Object });
    const openDownload = async (path) => {
        await checkLoginWork(() => open(config.serverAdress + '/api/upload/download/file/' + path));
    }
</script>

<template>
    <div class="file-message">
        <div class="file-lazy" @click="openDownload(chatMessage.message.content)">
            <div class="icon-box">
                <svg t="1730440088756" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5577"><path d="M752 80H272c-70.4 0-128 57.6-128 128v608c0 70.4 57.6 128 128 128h353.6c33.6 0 65.6-12.8 91.2-36.8l126.4-126.4c24-24 36.8-56 36.8-91.2V208c0-70.4-57.6-128-128-128zM208 816V208c0-35.2 28.8-64 64-64h480c35.2 0 64 28.8 64 64v464h-96c-70.4 0-128 57.6-128 128v80H272c-35.2 0-64-28.8-64-64z m462.4 44.8c-4.8 4.8-9.6 8-14.4 11.2V800c0-35.2 28.8-64 64-64h75.2l-124.8 124.8z" p-id="5578"></path><path d="M368 352h288c17.6 0 32-14.4 32-32s-14.4-32-32-32H368c-17.6 0-32 14.4-32 32s14.4 32 32 32zM496 608h-128c-17.6 0-32 14.4-32 32s14.4 32 32 32h128c17.6 0 32-14.4 32-32s-14.4-32-32-32zM368 512h288c17.6 0 32-14.4 32-32s-14.4-32-32-32H368c-17.6 0-32 14.4-32 32s14.4 32 32 32z" p-id="5579"></path></svg>
            </div>
            <div class="content-box">分享了文件</div>
        </div>
    </div>
</template>

<style>
    @import url("./styles/ChatFileMessage.css");
</style>
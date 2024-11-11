<script setup>
    import { ref } from 'vue'
    import config from '@/scripts/config'

    const showStatus = ref(false);
    const chatMessage = defineProps({ message: Object });
</script>

<template>
    <div class="image-message">
        <div class="image-lazy" v-if="!showStatus" @click="showStatus = true">
            <div class="icon-box">
                <svg t="1730990674177" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4721"><path d="M887.466667 136.533333H136.533333a68.266667 68.266667 0 0 0-68.266666 68.266667v614.4a68.266667 68.266667 0 0 0 68.266666 68.266667h750.933334a68.266667 68.266667 0 0 0 68.266666-68.266667V204.8a68.266667 68.266667 0 0 0-68.266666-68.266667zM136.533333 204.8h750.933334v614.4H136.533333V204.8z" p-id="4722"></path><path d="M358.4 375.466667a51.2 51.2 0 1 1-102.4 0 51.2 51.2 0 0 1 102.4 0" p-id="4723"></path><path d="M750.933333 409.6l48.264534 48.264533-204.8 204.8a34.133333 34.133333 0 0 1-48.264534 0l-112.401066-112.384-163.601067 163.584L221.866667 665.6l187.733333-187.733333a34.133333 34.133333 0 0 1 48.264533 0l112.401067 112.384L750.933333 409.6z" p-id="4724"></path></svg>
            </div>
            <div class="content-box">分享了图片</div>
        </div>
        <el-image v-else :src="config.serverAdress + config.serverApi.downloadImages + chatMessage.message.content" :zoom-rate="1.2" :max-scale="7" :min-scale="0.2" :preview-src-list="[config.serverAdress + config.serverApi.downloadImages + chatMessage.message.content]" fit="cover"/>
    </div>
</template>

<style>
    @import url("./styles/ChatPngMessage.css");
</style>
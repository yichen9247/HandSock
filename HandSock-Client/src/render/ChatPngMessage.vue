<script setup lang="ts">
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import HandUtils from '@/scripts/HandUtils'

    const applicationStore = utils.useApplicationStore();

    /**
     * Controls visibility state of full image preview
     * @type {import('vue').Ref<boolean>}
     */
    const showStatus: Ref<boolean> = ref(socket.server.config.autoShowImage);

    /**
     * Props definition
     * @type {Object}
     * @property {Object} message - Message object containing image content
     */
    const chatMessage: any = defineProps({
        message: { type: Object, required: true }
    });

    /**
     * Constructs the full image URL from server config and message content
     * @returns {string} Complete image URL
     */
    const getImageUrl = (): string => {
        return socket.server.config.serverUrl + socket.server.downloadImages + chatMessage.message.content;
    }

    const previewImage = async (): Promise<void> => {
        await HandUtils.checkClientLoginStatus(() => {
            showStatus.value = true
        });
    }
</script>

<template>
    <div class="image-message" :style="{ border: (showStatus && applicationStore.loginStatus) || applicationStore.isDeviceMobile ? 'none' : '1px solid #c5ccdf' }">
        <!-- Image placeholder with icon when collapsed -->
        <div class="image-lazy" v-if="!showStatus || !applicationStore.loginStatus" @click="previewImage">
            <div class="icon-box">
                <svg t="1737351424365" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="11308" width="36" height="36"><path d="M124.7744 757.1968l150.2208-147.0976c20.736-20.3264 54.272-19.3024 73.7792 2.2016l66.0992 72.96c19.8656 21.9136 54.1696 22.4768 74.752 1.2288l194.9696-201.5744c20.4288-21.0944 54.3744-20.736 74.3424 0.768l148.8896 160.6144v187.9552s-2.5088 75.8272-105.7792 75.8272H208.9984s-84.224-9.1648-84.224-92.8256v-60.0576z" fill="var(--dominColor)" p-id="11309"></path><path d="M369.2032 474.2144c62.3616 0 113.0496-50.7392 113.0496-113.0496s-50.7392-113.0496-113.0496-113.0496-113.0496 50.7392-113.0496 113.0496 50.7392 113.0496 113.0496 113.0496z m0-164.7104c28.4672 0 51.6096 23.1424 51.6096 51.6096s-23.1424 51.6096-51.6096 51.6096-51.6096-23.1424-51.6096-51.6096 23.1424-51.6096 51.6096-51.6096z" fill="#333333" p-id="11310"></path><path d="M819.8656 82.0224H208.9984c-73.4208 0-133.12 59.6992-133.12 133.12v592.64c0 73.4208 59.6992 133.12 133.12 133.12h610.9184c73.4208 0 133.12-59.6992 133.12-133.12V215.1424c-0.0512-73.4208-59.7504-133.12-133.1712-133.12z m-610.8672 61.44h610.9184c39.5264 0 71.68 32.1536 71.68 71.68V583.68l-110.08-118.784a82.048 82.048 0 0 0-59.1872-26.2144h-0.8704c-22.1184 0-43.52 9.0624-58.88 24.9856l-194.9696 201.5744c-3.9936 4.096-9.2672 6.144-15.0528 6.2464a20.4544 20.4544 0 0 1-14.848-6.7072l-66.0992-72.96c-15.104-16.64-35.7888-26.2144-58.2656-26.88-22.4256-0.6656-43.6736 7.6288-59.7504 23.3472l-116.2752 113.664V215.1424c0-39.5264 32.1536-71.68 71.68-71.68z m610.8672 736H208.9984c-39.5264 0-71.68-32.1536-71.68-71.68v-22.5792a30.208 30.208 0 0 0 8.96-5.9904l150.2208-147.0976c3.9936-3.9424 9.3184-5.9392 14.9504-5.8368 5.632 0.1536 10.8032 2.56 14.592 6.7072l66.0992 72.96c15.1552 16.7424 36.8128 26.5216 59.3408 26.88 22.6816 0.1536 44.544-8.704 60.2112-24.9344l194.9696-201.5744c3.8912-4.0448 9.1136-6.2464 14.6944-6.2464h0.2048c5.6832 0.0512 10.9568 2.4064 14.7968 6.5536l148.8896 160.6144c1.8944 2.048 4.0448 3.6352 6.2976 5.0688v135.424c0 39.5776-32.1536 71.7312-71.68 71.7312z" fill="#333333" p-id="11311"></path></svg>
            </div>
            <div class="content-box-h">
                <span class="box-text">发送了图片</span>
                <span class="box-desc">注意甄别，谨慎查看</span>
            </div>
        </div>

        <!-- Full image preview when expanded -->
        <transition name="van-fade">
            <el-image v-if="applicationStore.loginStatus" v-show="showStatus" :src="getImageUrl()" @click="HandUtils.previewImageBySwal({ 
                src: getImageUrl(), 
                html: `发送时间： <span style='color: var(--dominColor)'>${chatMessage.message.time}</span>` 
            })" fit="cover"/>
        </transition>
    </div>
</template>

<style lang="less">
    @import url("./styles/ChatPngMessage.less");
</style>
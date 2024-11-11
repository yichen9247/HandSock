<script setup>
    import utils from "@/scripts/utils.js"
    import AdminFrame from "@/frame/AdminFrame.vue"
    import { Menu } from "@element-plus/icons-vue"
    import APPDialog from "@/dialog/APPDialog.vue"
    import ChatContent from "@/model/ChatContent.vue"
    import ChatFunction from '@/model/ChatFunction.vue'
    import ChantChannel from '@/model/ChantChannel.vue'
    import ChatSideBarV2 from '@/model/ChatSideBarV2.vue'
    import MobileContain from "@/mobile/MobileContain.vue"
    
    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <div :class="`main-container ${applicationStore.isDeviceMobile ? 'container-mobile' : 'container-pc' }`" :style="{ minWidth: applicationStore.isDeviceMobile ? 'unset' : '1024px' }">
        <ChatSideBarV2 style="width: 80px;"/>
        <ChantChannel style="width: 260px;" v-if="!applicationStore.isDeviceMobile"/>

        <div class="chat-content">
            <div class="content-head">
                <el-button class="h-button" v-if="applicationStore.isDeviceMobile" :icon="Menu" @click="utils.openForMobileDrawer"></el-button>
                <span class="group-name">{{ applicationStore.groupInfo.name }}</span>
            </div>
            <ChatContent/>
        </div>

        <audio id="audioRef" style="display: none!important;"></audio>
        <ChatFunction v-if="!applicationStore.isDeviceMobile"/><APPDialog/><MobileContain v-if="applicationStore.isDeviceMobile"/>
        <AdminFrame v-if="applicationStore.loginStatus && !applicationStore.isDeviceMobile && applicationStore.userList.length > 0 && applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid).isAdmin" :style="{ transform: onelDialogStore.adminFrameStatus ? 'translateX(100px)' : 'translateX(-100%)' }"/>
    </div>
</template>

<style>
    @import url("./styles/MainLayout.css");
</style>
<!--
 * @Description: Main layout component that manages the overall chat application structure
 * @Author: Hua
 * @Date: 2024-11-25
 * @Components:
 *   ChatSideBarV2 - Side navigation bar
 *   ChantChannel - Channel list for desktop view
 *   ChatContent - Main chat content area
 *   ChatFunction - Additional chat functions for desktop
 *   APPDialog - Application dialog component
 *   MobileContain - Mobile specific container
 *   AdminFrame - Admin control panel
 * @Features:
 *   - Responsive layout for mobile and desktop
 *   - Conditional rendering based on device type
 *   - Admin panel access control
-->

<script setup lang="ts">
    import utils from "@/scripts/utils"
    import socket from "@/socket/socket"
    import { useRouter } from 'vue-router'
    import { groupInfoType } from "../../types"
    import HandUtils from "@/scripts/HandUtils"
    import release from "@/assets/image/app-release.png"

    const router = useRouter();
    const appDialogStatus = ref(false);
    const actionShow: Ref<boolean> = ref(false);
    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    const isAdmin = (): any => {
        return applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid )?.isAdmin;
    }
    
    const onHeadClick = (): void => {
        if (applicationStore.isDeviceMobile) actionShow.value = true;
    }

    onMounted(async () => {
        if (socket.server.config.appDialog && localStorage.getItem('app-dialog') !== 'true' && applicationStore.isDeviceMobile && !onelDialogStore.userLoginCenter && !actionShow.value) {
            setTimeout(async () => {
                appDialogStatus.value = true;
                localStorage.setItem('app-dialog', 'true');
            }, 1500);
        }
    });

    const openAppDownload = (): Window => open(socket.application.appDownload);
    const onActionSelect = async (item: groupInfoType): Promise<void> => HandUtils.toggleChatChannel(router, item.gid);
</script>

<template>
    <div :class="`main-container ${applicationStore.isDeviceMobile ? 'container-mobile' : 'container-pc'}`" :style="{ minWidth: applicationStore.isDeviceMobile ? 'unset' : '1024px' }">
        <ChatSideBarV2 style="width: 105px; min-width: 105px;"/>
        <ChatChannel v-if="!applicationStore.isDeviceMobile" style="width: 260px;"/>

        <!-- Main chat content area -->
        <div class="chat-content">
            <div class="content-head" @click="onHeadClick()">
                <span class="group-name">{{ applicationStore.groupInfo.name }}</span>
            </div>
            <ChatContent/>
        </div>

        <!-- Audio element for notifications -->
        <audio id="audioRef" style="display: none!important;"></audio>

        <!-- Additional components -->
        <ChatFunction v-if="!applicationStore.isDeviceMobile"/>
        <APPDialog/>

        <!-- Admin panel (desktop only) -->
        <AdminFrame
            :style="{ 
                transform: onelDialogStore.adminFrameStatus ? 'translateX(105px)' : 'translateX(-100%)'
            }"
            v-if="applicationStore.loginStatus && !applicationStore.isDeviceMobile && applicationStore.userList.length > 0 && isAdmin()"
        />

        <van-dialog v-model:show="appDialogStatus" title="推荐下载客户端" show-cancel-button message-align="justify" @confirm="openAppDownload">
            <div style="width: 100%; display: flex; justify-content: center; padding-bottom: 15px;">
                <img width="120" height="120" :src="release" alt="推荐下载客户端" draggable="false"/>
            </div>
        </van-dialog>
        <van-action-sheet v-if="applicationStore.isDeviceMobile" v-model:show="actionShow" :actions="applicationStore.chatGroupList" cancel-text="取消" close-on-click-action @select="onActionSelect"/>
    </div>
</template>

<style lang="less">
    @import url("./styles/MainLayout.less");
</style>
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
    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    import Swal from "sweetalert2";

    const isAdmin = (): any => {
        return applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid )?.isAdmin;
    }
</script>

<template>
    <div :class="`main-container ${applicationStore.isDeviceMobile ? 'container-mobile' : 'container-pc'}`" :style="{ minWidth: applicationStore.isDeviceMobile ? 'unset' : '1024px' }">
        <ChatSideBarV2 style="width: 105px; min-width: 105px;"/>
        <ChatChannel v-if="!applicationStore.isDeviceMobile" style="width: 260px;"/>

        <!-- Main chat content area -->
        <div class="chat-content">
            <div class="content-head">
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
    </div>
</template>

<style lang="less">
    @import url("./styles/MainLayout.less");
</style>
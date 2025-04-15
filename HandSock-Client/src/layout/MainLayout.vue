<script setup lang="ts">
    import utils from "@/scripts/utils"
    import socket from "@/socket/socket"
    import { useRouter } from 'vue-router'
    import { groupInfoType } from "../../types"
    import HandUtils from "@/scripts/HandUtils"
    import ForumContent from "@/forum/ForumContent.vue"
    import release from "@/assets/image/app-release.png"
    import NoticeContent from "@/notice/NoticeContent.vue"

    const router = useRouter();
    const appDialogStatus = ref(false);
    const actionShow: Ref<boolean> = ref(false);
    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();
    
    const onHeadClick = (): void => {
        if (applicationStore.isDeviceMobile) actionShow.value = true;
    }

    onMounted(async () => {
        if (socket.server.config.autoDialog && localStorage.getItem('app-dialog') !== 'true' && applicationStore.isDeviceMobile && !onelDialogStore.userLoginCenter && !actionShow.value) {
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
    <div :class="`main-container ${applicationStore.isDeviceMobile ? 'container-mobile' : 'container-pc'}`" :style="{ minWidth: applicationStore.isDeviceMobile ? 'unset' : '1240px' }">
        <ChatSideBarV2 style="width: 105px; min-width: 105px;"/>
        <ChatChannel v-if="!applicationStore.isDeviceMobile" style="width: 260px;"/>
        <div class="chat-content">
            <div class="content-head" @click="onHeadClick">
                <span class="group-name">{{ applicationStore.groupInfo.name }}</span>
                <div class="button-list" v-if="!applicationStore.isDeviceMobile">
                    <div class="toggle-button" @click="HandUtils.openCustomSwalDialog(NoticeContent, {
                        width: 515
                    })">
                        <svg t="1744160388051" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="7110" width="25" height="25"><path d="M69.818182 302.545455a23.272727 23.272727 0 0 1 23.272727-23.272728h186.181818l265.169455-189.952a23.272727 23.272727 0 0 1 36.817454 18.920728V913.92a23.272727 23.272727 0 0 1-36.747636 18.990545L279.272727 744.727273H93.090909a23.272727 23.272727 0 0 1-23.272727-23.272728V302.545455z m442.181818 519.028363V202.402909l-208.407273 146.594909h-164.072727L139.636364 674.210909h163.956363L512 821.573818zM750.708364 170.007273A383.767273 383.767273 0 0 1 960 512c0 146.106182-82.385455 277.597091-210.292364 342.597818l-28.997818-57.088A319.906909 319.906909 0 0 0 896 512c0-121.483636-68.189091-230.702545-174.498909-284.997818l29.207273-57.018182zM686.545455 733.090909l-28.974546-64.302545C791.272727 581.818182 791.272727 418.909091 647.400727 345.204364L686.545455 279.272727c186.181818 116.363636 197.818182 337.454545 0 453.818182z" fill="#ffffff" p-id="7111"></path></svg>
                    </div>

                    <div class="toggle-button" @click="HandUtils.openCustomSwalDialog(ForumContent, {
                        width: 515
                    })">
                        <svg t="1744160174908" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4690" width="25" height="25"><path d="M535.3472 919.9104h-311.296c-34.1504 0-61.9008-27.7504-61.9008-61.9008v-148.0704c-38.5536-67.7888-57.088-144.7936-53.6576-223.3344 4.5056-102.656 47.104-199.68 119.9616-273.2544C301.312 139.776 397.9264 96.256 500.5312 90.88c115.1488-5.9904 224.4608 34.1504 307.8144 113.2544 83.4048 79.1552 129.3312 186.0096 129.3312 300.9536 0 108.4416-41.5744 210.9952-117.0944 288.768-74.2912 76.544-173.4656 120.9856-279.7568 125.5424-1.792 0.3584-3.584 0.512-5.4784 0.512z m-12.288-768.1536c-6.4 0-12.8512 0.1536-19.3024 0.512-179.4048 9.4208-326.0928 157.44-333.9264 336.9984-3.0208 69.376 14.0288 137.3696 49.408 196.608 2.816 4.7616 4.352 10.1888 4.352 15.7184v156.3648c0 0.256 0.2048 0.4608 0.4608 0.4608h306.944c1.1264-0.1536 2.2528-0.256 3.3792-0.3072 191.6928-6.144 341.8624-161.2288 341.8624-353.0752 0-97.8944-39.1168-188.9792-110.1824-256.4096-66.3552-62.8224-152.0128-96.8704-242.9952-96.8704z" fill="#ffffff" p-id="4691"></path><path d="M517.3248 496.7936m-45.7728 0a45.7728 45.7728 0 1 0 91.5456 0 45.7728 45.7728 0 1 0-91.5456 0Z" fill="#ffffff" p-id="4692"></path><path d="M337.408 496.7936m-45.7728 0a45.7728 45.7728 0 1 0 91.5456 0 45.7728 45.7728 0 1 0-91.5456 0Z" fill="#ffffff" p-id="4693"></path><path d="M706.6112 496.7936m-45.7728 0a45.7728 45.7728 0 1 0 91.5456 0 45.7728 45.7728 0 1 0-91.5456 0Z" fill="#ffffff" p-id="4694"></path></svg>
                    </div>
                </div>
            </div>
            <div class="content-body">
                <ChatContent/>
            </div>
            <ChatFootBox/>
        </div>

        <LoginCenter v-if="!applicationStore.loginStatus"/>
        <ChatFunction v-if="!applicationStore.isDeviceMobile"/>
        <audio id="audioRef" style="display: none!important;"></audio>

        <Authority :permission="1">
            <AdminFrame v-if="!applicationStore.isDeviceMobile" :style="{ transform: onelDialogStore.adminFrameStatus ? 'translateX(105px)' : 'translateX(-100%)' }"/>
        </Authority>

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
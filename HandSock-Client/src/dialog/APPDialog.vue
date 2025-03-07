<!--
 * @Description: Application dialog component managing various modal dialogs and sheets
 * @Author: Hua
 * @Date: 2024-11-25
 * @Components:
 *   LoginCenter - Login dialog
 *   EmojeCenter - Emoji picker dialog  
 *   UploadCenter - File upload dialog
 *   UserCenter - User profile dialog/sheet
 *   PCSettingCenter - Settings dialog for desktop
 *   MobileSettings - Settings sheet for mobile
 * @Features:
 *   - Channel search functionality
 *   - Responsive dialogs for desktop/mobile
 *   - User profile management
 *   - Application settings
-->

<script setup>
    import utils from "@/scripts/utils"
    import { useRouter } from 'vue-router'
    import HandUtils from "@/scripts/HandUtils"

    const router = useRouter();
    const searchInput = ref("");
    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    // Search for and join a channel by ID
    const searchChannel = async () => {
        await HandUtils.searchChannelByGid(router, searchInput.value);
        searchInput.value = "";
    }
</script>

<template>
    <div class="dialog-content">
        <!-- Core dialog components -->
        <LoginCenter/><EmojeCenter/>

        <!-- Channel search dialog -->
        <el-dialog 
            class="search-center handsock-dialog" 
            v-model="onelDialogStore.searchalCenter" 
            width="450px" align-center destroy-on-close center style="padding: 0"
        >
            <p class="center-title">搜索频道</p>
            <div class="flex-line" style="display: flex; height: unset;">
                <el-input 
                    v-model="searchInput"
                    maxlength="5"
                    placeholder="请在此输入管理员提供的频道ID"
                    show-word-limit type="text" clearable validate-event
                />
                <el-button 
                    style="margin-left: 15px;"
                    type="primary" @click="searchChannel()"
                >
                    搜索频道
                </el-button>
            </div>
        </el-dialog>
        
        <!-- Desktop user profile dialog -->
        <el-dialog 
            v-if="!applicationStore.isDeviceMobile"
            class="user-center handsock-dialog"
            v-model="onelDialogStore.personalCenter"
            width="500px" align-center destroy-on-close center style="padding: 0;"
        >
            <UserCenter/>
        </el-dialog>

        <el-dialog 
            title="举报聊天记录" class="report-user"
            v-if="!applicationStore.isDeviceMobile"
            v-model="onelDialogStore.reportUserCenter"
            width="500px" align-center destroy-on-close  
        >
            <ReportUser/>
        </el-dialog>

        <van-action-sheet
            style="padding-top: 15px;"
            v-if="applicationStore.isDeviceMobile"
            v-model:show="onelDialogStore.reportUserCenter"
            @open="onelDialogStore.toggleFloatSheet"
        >
            <ReportUser style="width: 95%; margin: 0 auto; margin-bottom: 20px;"/>
        </van-action-sheet>

        <!-- Desktop settings dialog -->
        <el-dialog 
            v-if="!applicationStore.isDeviceMobile"
            v-model="onelDialogStore.settinglCenter"
            :class="`setting-center ${applicationStore.isDeviceMobile ? 'center-mobile' : 'center-pc'}`"
            width="620px" align-center center
        >
            <SettingCenter/>
        </el-dialog>
        
        <!-- Mobile user profile sheet -->
        <van-action-sheet 
            style="padding-top: 20px;"
            v-if="applicationStore.isDeviceMobile"
            v-model:show="onelDialogStore.personalCenter"
            @open="onelDialogStore.toggleFloatSheet"
        >
            <UserCenter style="margin-bottom: 20px;"/>
        </van-action-sheet>
    </div>
</template>

<style lang="less">
    @import url("./styles/APPDialog.less");
</style>
<script setup>
    import { ref } from "vue"
    import utils from "@/scripts/utils"
    import { useRouter } from 'vue-router'
    import { ElMessageBox } from 'element-plus'
    import UserCenter from "@/center/UserCenter.vue"
    import EmojeCenter from "./CenterDialog/EmojeCenter.vue"
    import LoginCenter from "./CenterDialog/LoginCenter.vue"
    import MobileSettings from "@/mobile/MobileSettings.vue"
    import UploadCenter from "./CenterDialog/UploadCenter.vue"
    import PCSettingCenter from "./SettingCenter/PCSettingCenter.vue"
    import { toggleChatChannel, sendSocketEmit } from '@/socket/socketClient'

    const router = useRouter();
    const searchInput = ref('');
    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    const searchChannel = async () => {
        if (!/^[-+]?\d+$/.test(searchInput.value)) 
            return utils.showToasts('warning', '输入格式不合规');
        if (applicationStore.groupInfo.uid === Number(searchInput.value)) 
            return utils.showToasts('warning', '请勿重复加入频道')

        await sendSocketEmit("[SEARCH:GROUP]", {
            gid: Number(searchInput.value)
        }, (data) => {
            searchInput.value = "";
            onelDialogStore.setSearchalCenter(false);
            if (data.code === 200) {
                ElMessageBox.confirm(`查找到频道【${data.data.name}】，是否加入？`, '搜索频道', {
                    type: 'warning',
                    cancelButtonText: '取消',
                    confirmButtonText: '进入频道'
                }).then(async () => {
                    utils.showToasts('success', "加入频道成功");
                    await toggleChatChannel(router, data.data.gid);
                }).catch(() => {});

            } else utils.showToasts('warning', '未找到该频道')
        });
    }
</script>

<template>
    <div class="dialog-content">
        <LoginCenter/><EmojeCenter/><UploadCenter/>
        <el-dialog class="search-center handsock-dialog" v-model="onelDialogStore.searchalCenter" width="450px" align-center destroy-on-close center style="padding: 0">
            <p class="center-title">搜索频道</p>
            <div class="flex-line" style="display: flex;">
                <el-input v-model="searchInput" maxlength="5" placeholder="请在此输入管理员提供的频道ID" show-word-limit type="text" clearable validate-event />
                <el-button type="primary" @click="searchChannel()" style="margin-left: 15px;">搜索频道</el-button>
            </div>
        </el-dialog>
        
        <el-dialog class="user-center handsock-dialog" v-model="onelDialogStore.personalCenter" :width="'500px'" align-center destroy-on-close center v-if="!applicationStore.isDeviceMobile">
            <UserCenter/>
        </el-dialog>

        <el-dialog :class="`setting-center ${applicationStore.isDeviceMobile ? 'center-mobile' : 'center-pc'}`" v-model="onelDialogStore.settinglCenter" width="650px" align-center destroy-on-close center v-if="!applicationStore.isDeviceMobile">
            <PCSettingCenter/>
        </el-dialog>
        
        <van-action-sheet v-model:show="onelDialogStore.personalCenter" style="padding-top: 20px;" v-if="applicationStore.isDeviceMobile" @open="onelDialogStore.toggleFloatSheet">
            <UserCenter style="margin-bottom: 20px;"/>
        </van-action-sheet>

        <van-action-sheet v-model:show="onelDialogStore.settinglCenter" style="padding-top: 15px;" v-if="applicationStore.isDeviceMobile" @open="onelDialogStore.toggleFloatSheet">
            <MobileSettings style="margin-bottom: 20px;"/>
        </van-action-sheet>
    </div>
</template>

<style>
    @import url("./styles/APPDialog.css");
</style>
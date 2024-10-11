<script setup>
    import { ref, watch } from "vue"
    import utils from "@/scripts/utils"
    import channelList from "../scripts/channel.js"
    import socketClient from "@/socket/socketClient.js"
    import { ElMessage, ElMessageBox } from 'element-plus'

    import MSettings from "@/dialog/Settings/MSettings.vue"
    import PSettings from "@/dialog/Settings/PSettings.vue"

    const avatarInput = ref('');
    const searchInput = ref('');
    const nameInfoInput = ref('');
    const onlineChatStore = utils.useOnlineChatStore();
    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    const setUserAvatar = () => {
        if (avatarInput.value === "") return ElMessage({ message: 'QQ号请不要为空！', type: 'warning' });
        onlineChatStore.setUserQQ(avatarInput.value);
        localStorage.setItem('userqq', avatarInput.value);
        ElMessage({ message: '设置头像成功！', type: 'success' });
    }

    const setUserNameInfo = async () => {
        if (nameInfoInput.value === "") return ElMessage({ message: '昵称请不要为空！', type: 'warning' });
        onlineChatStore.setUserName(nameInfoInput.value);
        localStorage.setItem('username', nameInfoInput.value);
        ElMessage({ message: '设置昵称成功！', type: 'success' });
    }

    const searchChannel = () => {
        if (!onlineChatStore.logind || !onlineChatStore.connection) return ElMessage({ message: '你还没有登录到聊天室哦！', type: 'warning' });
        if (onlineChatStore.chatChannel === Number(searchInput.value)) return ElMessage({ message: '请勿重复加入频道！', type: 'warning' });
        if (!channelList.some(item => item.id === Number(searchInput.value))) return ElMessage({ message: '未找到该频道！', type: 'warning' });
        
        onelDialogStore.setSearchalCenter(false);
        let channelName = channelList.find(item => item.id === Number(searchInput.value)).name;
        ElMessageBox.confirm(`查找到频道【${channelName}】，是否加入？`, '搜索频道', {
            type: 'warning',
            cancelButtonText: '取消',
            confirmButtonText: '进入频道'
        }).then(async () => {
            onlineChatStore.setChatChannel(Number(searchInput.value));
            history.pushState(null,null,`/?channel=${Number(searchInput.value)}`);
            await socketClient.sendJoinMessage(Number(searchInput.value));
            searchInput.value = "";
            utils.showToasts('success', '加入频道成功');
        }).catch(() => {});
    }

    const circleUrl = ref("https://dn-qiniu-avatar.qbox.me/avatar/");
    watch(onlineChatStore,() => {
        circleUrl.value = onlineChatStore.userqq === 0 ? "https://dn-qiniu-avatar.qbox.me/avatar/" : `https://q2.qlogo.cn/headimg_dl?dst_uin=${onlineChatStore.userqq}&spec=5`;
    },{deep:true});
</script>

<template>
    <div class="dialog-content">
        <el-dialog v-model="onelDialogStore.personalCenter" title="个人中心" width="25%" align-center destroy-on-close center>
            <div class="personCenter" style="color: #000000">
                <div class="info-line" style="height: 70px;">
                    <el-image class="avatar" shape="square" size="small" :src="circleUrl" :preview-src-list="[circleUrl]"/>
                    <div class="avatar-edit-content">
                        <span class="title-text" style="color: #000000">设置头像</span>
                        <div class="flex-line">
                            <el-input v-model="avatarInput" maxlength="12" placeholder="请输入你的QQ号以获取头像" show-word-limit type="text" clearable validate-event />
                            <el-button type="primary" @click="setUserAvatar()">设置头像</el-button>
                        </div>
                    </div>
                </div>
                
                <div class="info-line">
                    <div class="flex-line">
                        <span class="title-text" style="margin-right: 10px; color: #000000">设置昵称：</span>
                        <div class="flex-line">
                            <el-input v-model="nameInfoInput" maxlength="5" placeholder="请在此输入合法的互联网昵称" show-word-limit type="text" clearable validate-event />
                            <el-button type="primary" @click="setUserNameInfo()">设置昵称</el-button>
                        </div>
                    </div>
                </div>
    
                <div class="info-lines">
                    <span class="id-info" v-text="channelList.find(item => item.id === onlineChatStore.chatChannel).admin === onlineChatStore.userid ? '用户身份：管理员' : '用户身份：普通用户'"></span>
                    &nbsp;&nbsp;&nbsp;<span class="id-info">用户号码：{{ onlineChatStore.userid }}</span>
                </div>
            </div>
        </el-dialog>
    
        <el-dialog v-model="onelDialogStore.searchalCenter" title="搜索频道" width="450px" align-center destroy-on-close center>
            <div class="flex-line" style="display: flex;">
                <el-input v-model="searchInput" maxlength="5" placeholder="请在此输入管理员提供的频道ID" show-word-limit type="text" clearable validate-event />
                <el-button type="primary" @click="searchChannel()" style="margin-left: 15px;">搜索频道</el-button>
            </div>
        </el-dialog>
        
        <el-dialog v-model="onelDialogStore.settinglCenter" title="系统设置" width="650px" align-center destroy-on-close center>
            <PSettings v-if="!applicationStore.isDeviceMobile"/>
            <MSettings v-else/>
        </el-dialog>
    </div>
</template>

<style>
    @import url("./styles/AppDialog.css");
</style>
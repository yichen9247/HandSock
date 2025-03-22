<!--
 * @Description: 在线用户组件，显示用户头像、昵称和在线状态
 * @Author: Hua
 * @Date: 2024-11-25
-->

<script setup>
    import utils from "@/scripts/utils"
    import socket from "@/socket/socket"
    import HandUtils from "@/scripts/HandUtils"

    const props = defineProps({
        user: {
            type: Object, required: true
        }
    });

    const applicationStore = utils.useApplicationStore();
    const userInfo = computed(() => HandUtils.getUserInfoByUid(props.user.uid));
    
    const avatarUrl = computed(() => 
        socket.server.config.serverUrl + socket.server.downloadAvatar + userInfo.value.avatar
    );
</script>

<template>
    <div 
        class="user-item mobile-bg" 
        v-if="props.user.login && applicationStore.userList.some(item => item.uid === props.user.uid)"
    >
        <el-tooltip 
            class="box-item" 
            effect="handsock" 
            placement="right" 
            :content="props.user.platform"
            v-if="!applicationStore.isDeviceMobile"
        >
            <div class="item-content">
                <img 
                    class="user-avatar" 
                    :src="avatarUrl" 
                    draggable="false"
                    :alt="userInfo.nick"
                >
                <div class="content">
                    <span class="user-nick">{{ userInfo.nick }}</span>
                    <span class="user-radio"></span>
                </div>
            </div>
        </el-tooltip>
        <div class="item-content" v-else>
            <img 
                class="user-avatar" 
                :src="avatarUrl" 
                draggable="false"
                :alt="userInfo.nick"
            >
            <div class="content">
                <span class="user-nick">{{ userInfo.nick }}</span>
                <span class="user-radio"></span>
            </div>
        </div>
    </div>
</template>

<style lang="less">
    div.el-popper.is-handsock {
        border-radius: 6px;
        background: #ffffff;
        border: 1px solid #c5ccdf;
        color: rgba(60, 60, 67, 0.65);
    }

    div.el-popper.is-handsock span.el-popper__arrow::before {
        display: none;
    }
</style>
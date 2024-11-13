<script setup>
    import utils from "@/scripts/utils"
    import config from "@/scripts/config"

    const props = defineProps({ user: Object });
    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <div class="user-item" v-if="props.user.login && applicationStore.userList.some(item => item.uid === props.user.uid)">
        <el-tooltip class="box-item" effect="handsock" placement="right" :content="props.user.platform" >
            <div class="item-content">
                <img class="user-avatar" :src="config.serverAdress + config.serverApi.downloadAvatar + utils.queryUserInfo(props.user.uid).avatar">
                <span class="user-nick">{{ utils.queryUserInfo(props.user.uid).nick }}</span>
                <span class="user-radio"></span>
            </div>
        </el-tooltip>
    </div>
</template>

<style lang="less">
    div.el-popper.is-handsock {
        color: #ffffff;
        background: var(--dominColor);
        border: 1px solid rgba(255, 255, 255, 0.35);
    }

    div.el-popper.is-handsock span.el-popper__arrow::before {
        background: var(--dominColor);
    }
</style>
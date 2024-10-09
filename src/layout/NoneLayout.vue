<!-- eslint-disable no-undef -->
<script setup>
    import utils from "@/scripts/utils"

    const onlineChatStore = utils.useOnlineChatStore();
    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    const searchFromChannel = () => {
        if (onlineChatStore.logind && onlineChatStore.connection) {
            onelDialogStore.setSearchalCenter(true);
        } else {
            if (applicationStore.isDeviceMobile) {
                showFailToast('用户未登录');
            } else ElMessage({ message: '你还没有登录到聊天室哦！', type: 'warning' });
        }
    }
</script>

<template>
    <div id="search-main">
        <el-empty description=" " @click="searchFromChannel"/>
    </div>
</template>

<style>
    @import url("./styles/NoneLayout.css");
</style>
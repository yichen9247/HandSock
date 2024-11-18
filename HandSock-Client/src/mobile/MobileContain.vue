<script setup>
    import utils from "@/scripts/utils"

    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <el-drawer v-model="onelDialogStore.mobilesidebars" size="80%" :show-close="false" class="mobile-side-bar" :destroy-on-close="true" title="SideBar" :with-header="false" direction="ltr" v-if="applicationStore.isDeviceMobile">
        <MobileDrawer/>
    </el-drawer>
</template>

<style>
    @import url("./styles/MobileContain.css");
</style>
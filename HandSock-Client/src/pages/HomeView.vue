<script setup>
    import { onMounted } from "vue"
    import utils from "@/scripts/utils"
    import { startSocketIo } from "@/socket/socketClient.js"
    
    onMounted(async () => await startSocketIo());
    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <transition name="el-fade-in">
        <MainLayout v-show="applicationStore.isSiteReadyStatus"/>
    </transition>
</template>
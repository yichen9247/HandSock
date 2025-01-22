<script setup lang="ts">
    import utils from "@/scripts/utils"
    import { startSocketIo } from "@/socket/socketClient"

    const applicationStore = utils.useApplicationStore();
    onMounted(async (): Promise<void> => await startSocketIo());
</script>

<template>
    <transition name="el-fade-in">
        <MainLayout v-show="applicationStore.isSiteReadyStatus"/>
    </transition>
</template>
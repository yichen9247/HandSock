<script setup lang="ts">
    import { startSocketIo } from "@/socket/socketClient"
    onMounted(async (): Promise<void> => await startSocketIo());
</script>

<template>
    <transition name="el-fade-in">
        <MainLayout/>
    </transition>
</template>
<script setup>
    import utils from "@/scripts/utils";

    const props = defineProps({
        permission: { type: Number, required: true }
    });

    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <slot v-if="applicationStore.userInfo.permission === props.permission && applicationStore.loginStatus"/>
</template>
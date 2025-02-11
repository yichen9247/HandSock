<script setup lang="ts">
    import utils from "@/scripts/utils"
    import socket from "@/socket/socket"
    import { restfulType } from '../../../types'
    import { sendSocketEmit } from "@/socket/socketClient"

    const playlist: Ref<string> = ref(null);
    sendSocketEmit(socket.send.Get.GetSystemPlaylist, null, (response: restfulType): void => {
        if (response.code === 200) {
            playlist.value = response.data;
        } else utils.showToasts('error', response.message);
    });
</script>

<template>
    <div class="background-music">
        <iframe v-if="playlist !== null" class="audio-iframe" frameborder="no" border="0" marginwidth="0" marginheight="0" width="100%" height=410 :src="`https://music.163.com/outchain/player?type=0&id=${playlist}&auto=1&height=430`"></iframe>
    </div>
</template>
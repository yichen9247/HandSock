<script setup>
  import utils from "@/scripts/utils"
  import NoneMain from "@/layout/NoneLayout.vue"
  import AppDialog from "@/dialog/AppDialog.vue"
  import channelList from "../scripts/channel.js"
  import SideBar from "@/model/ChatSideBars.vue"
  import ChatContent from "@/model/ChatContent.vue"
  import socketClient from "@/socket/socketClient.js"
  import MobileContiner from "@/mobile/MobileContiner.vue"
  
  const onlineChatStore = utils.useOnlineChatStore();
  const applicationStore = utils.useApplicationStore();

  socketClient.startSocketIo();
</script>

<template>
  <transition name="el-fade-in">
    <main id="pagemain" v-show="applicationStore.isSiteReadyStatus">
      <NoneMain v-if="onlineChatStore.chatChannel === 0 && !channelList.some(item => item.id === 0)"/>
      <MobileContiner v-else/><AppDialog/><SideBar/><ChatContent/>
    </main>
  </transition>
</template>
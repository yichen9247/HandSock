<!-- eslint-disable no-unused-vars -->

<script>
  import { RouterView } from 'vue-router'
  if (localStorage.getItem('audio-switch') !== 'true' && localStorage.getItem('audio-switch') !== 'false') localStorage.setItem('audio-switch', true);
</script>

<template>
  <RouterView/>
</template>
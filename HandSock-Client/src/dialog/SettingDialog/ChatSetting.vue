
<script setup>
    import { ref } from "vue"
    import utils from "@/scripts/utils"
    import { Check, Close } from '@element-plus/icons-vue'
    const audioSelect = ref(localStorage.getItem('audio'));
    const audioSwitch = ref(localStorage.getItem('audio-switch') === 'true' ? true : false);

    const audioSelectChange = async (event) => {
        localStorage.setItem('audio', event);
        await utils.playNoticeVoice();
        await utils.showToasts('success' ,'切换提示音成功');
    }
    const audioSwitchChange = async (event) => localStorage.setItem('audio-switch', event);
</script>

<template>
    <div class="chat-setting">
        <p class="set-title">声音提醒</p>
        <div class="switch-item">
            <el-switch v-model="audioSwitch" size="large" inline-prompt :active-icon="Check" :inactive-icon="Close" @change="audioSwitchChange"/>
            <span class="label">{{ audioSwitch ? '开' : '关' }}</span>
        </div>

        <p class="set-title">提示声音</p>
        <el-radio-group v-model="audioSelect" size="large" class="audio-group" @change="audioSelectChange">
            <el-radio-button label="默认" value="default" />
            <el-radio-button label="苹果" value="apple" />
            <el-radio-button label="陌陌" value="momo" />
            <el-radio-button label="滑稽" value="huaji" />
        </el-radio-group>
    </div>
</template>

<style>
    @import url("./styles/ChatSetting.css");
</style>
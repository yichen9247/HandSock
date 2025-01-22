<!--
 * @Description: Chat settings component for managing notification sounds and preferences
 * @Author: Hua
 * @Date: 2024-11-25
 * @Features:
 *   - Real-time sound preview
 *   - Persistent settings storage
 *   - Select notification sound type
 *   - Toggle notification sounds on/off
-->

<script setup lang="ts">
    import utils from "@/scripts/utils"
    import { Check, Close } from '@element-plus/icons-vue'
    import { playNoticeVoice } from "@/scripts/audioUtils"

    // Initialize reactive refs from localStorage
    const audioSelect = ref(localStorage.getItem('audio'));
    const audioSwitch = ref(localStorage.getItem('audio-switch') === 'true');

    /**
     * Handle notification sound type change
     * @param {string} selectedSound - The newly selected sound value
     */
    const audioSelectChange = async (selectedSound: string): Promise<void> => {
        localStorage.setItem('audio', selectedSound);
        await playNoticeVoice();
        await utils.showToasts('success', '切换提示音成功');
    }

    /**
     * Handle notification sound toggle
     * @param {boolean} enabled - Whether notifications are enabled
     */
    const audioSwitchChange = async (enabled: string): Promise<void> => {
        localStorage.setItem('audio-switch', enabled);
    }
</script>

<template>
    <div class="chat-setting">
        <p class="set-title">声音提醒</p>
        <div class="switch-item">
            <el-switch 
                size="large"
                inline-prompt
                :active-icon="Check"
                v-model="audioSwitch"
                :inactive-icon="Close"
                @change="audioSwitchChange"
            />
            <span class="label">{{ audioSwitch ? '开' : '关' }}</span>
        </div>

        <p class="set-title">提示声音</p>
        <el-radio-group
            size="large"
            class="audio-group"
            v-model="audioSelect"
            @change="audioSelectChange"
        >
            <el-radio-button label="默认" value="default" />
            <el-radio-button label="苹果" value="apple" />
            <el-radio-button label="陌陌" value="momo" />
            <el-radio-button label="滑稽" value="huaji" />
        </el-radio-group>
    </div>
</template>

<style lang="less">
    div.chat-setting {
        width: 100%;

        p.set-title {
            font-size: 16px;
        }

        div.audio-group {
            width: 100%;
            margin: 5px 0;
            margin-top: 15px;
        
            label {
                flex: 1;
                width: 25%;
            
                span {
                    width: 100%;
                }
            }
        }

        div.switch-item {
            display: flex;
            margin-top: 5px;
            margin-bottom: 10px;
            align-items: center;

            span.label {
                margin-top: 2.2px;
                margin-left: 10px;
            }
        }
    }
</style>
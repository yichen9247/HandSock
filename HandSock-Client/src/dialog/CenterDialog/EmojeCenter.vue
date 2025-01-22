<!--
 * @Description: Emoji picker dialog component for chat input
 * @Author: Hua
 * @Date: 2024-11-25
 * @Features:
 *   - Click to insert emoji
 *   - Responsive dialog width
 *   - Auto-close on selection
 *   - Common emoji categories
 *   - Grid layout of emoji options
-->

<script setup lang="ts">
    import utils from "@/scripts/utils"
    
    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    // Categorized emoji list for better organization
    const emojeList: Array<string> = [
        // Smileys & Emotion
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍",
        "😘", "😗", "😙", "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩", "😏",
        
        // Negative Emotions  
        "😒", "😞", "😔", "😟", "😕", "🙁", "😣", "😖", "😫", "😩", "😢", "😭", "😮", "💨", "😤",
        "😠", "😡", "🤬", "🤯", "😳", "😱", "😨", "😰", "😥", "😓",
        
        // Face Actions
        "🤗", "🤔", "🤭", "🤫", "🤥", "😶", "🌫️", "😐", "😑", "😬", "🙄", "😯", "😦", "😧", "😮",
        "😲", "😴", "🤤", "😪", "😵", "💫", "🤐", "🤢", "🤮", "🤧",
        
        // Special Characters
        "😷", "🤒", "🤕", "🤑", "🤠", "😈", "👿", "👹", "👺", "🤡", "💩", "👻", "💀", "👽", "👾",
        "🤖", "🎃", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾", "🐶"
    ]

    /**
     * Insert selected emoji into chat input
     * @param {string} emoje - Selected emoji character
     */
    const renderEmoje = (emoje: string): void => {
        applicationStore.chantInput += emoje;
        onelDialogStore.setEmojeListCenter(false);
    }
</script>

<template>
    <el-dialog 
        class="emoje-center"
        v-model="onelDialogStore.emojeListCenter"
        :width="applicationStore.isDeviceMobile ? '95%' : '500px'"
        title="键入表情" 
        align-center 
        destroy-on-close 
        center
    >
        <div class="emoje-list">
            <span 
                v-for="(item, index) in emojeList" 
                :key="index"
                class="emoje-item" 
                @click="renderEmoje(item)"
            >
                {{ item }}
            </span>
        </div>
    </el-dialog>
</template>

<style lang="less">
    @import url("./styles/EmojeCenter.less");
</style>
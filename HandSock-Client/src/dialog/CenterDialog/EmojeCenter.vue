
<script setup>
    import utils from "@/scripts/utils"
    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    const emojeList = [
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", 
        "😘", "😗", "😙", "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩", "😏", 
        "😒", "😞", "😔", "😟", "😕", "🙁", "😣", "😖", "😫", "😩", "😢", "😭", "😮", "💨", "😤", 
        "😠", "😡", "🤬", "🤯", "😳", "😱", "😨", "😰", "😥", "😓", "🤗", "🤔", "🤭", "🤫", "🤥", 
        "😶", "🌫️", "😐", "😑", "😬", "🙄", "😯", "😦", "😧", "😮", "😲", "😴", "🤤", "😪", "😵", 
        "💫", "🤐", "🤢", "🤮", "🤧", "😷", "🤒", "🤕", "🤑", "🤠", "😈", "👿", "👹", "👺", "🤡",
        "💩", "👻", "💀", "👽", "👾", "🤖", "🎃", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾", "🐶"
    ];

    const renderEmoje = (emoje) => {
        applicationStore.chantInput += emoje;
        onelDialogStore.setEmojeListCenter(false);
    }
</script>

<template>
    <el-dialog class="emoje-center" v-model="onelDialogStore.emojeListCenter" :width="applicationStore.isDeviceMobile ? '95%' : '500px'" title="键入表情" align-center destroy-on-close center >
        <div class="emoje-list">
            <span class="emoje-item" v-for="(item, index) in emojeList" :key="index" @click="renderEmoje(item)">{{ item }}</span>
        </div>
    </el-dialog>
</template>

<style>
    @import url("./styles/EmojeCenter.css");
</style>
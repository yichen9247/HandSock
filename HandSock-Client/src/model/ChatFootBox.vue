<script setup>
    import config from "@/scripts/config"
    import utils from "@/scripts/utils.js"
    import { sendChatMessage, checkLoginWork, sendSocketEmit } from "@/socket/socketClient.js"

    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    const sendChatMessageV2 = async () => {
        await checkLoginWork(async () => {
            if (applicationStore.groupClosed) 
                return utils.showErrorToasts('error', '频道未开启', '该频道暂未开启！');
            if (applicationStore.chantInput === "") 
                return utils.showErrorToasts('warning', '格式不正确', '请参照正确的聊天内容格式！');
            await sendChatMessage();
        });
    }
    const handleKeydown = async (event) => (event.ctrlKey && event.key === "Enter") && await sendChatMessage();

    const uploadFileSuccess = async (data) => {
        if (data !== null && data.response !== null) {
            if (data.code === 200) {
                await sendSocketEmit("[SEND:MESSAGE]", {
                    type: "image",
                    content: data.data.path
                }, (response) => {
                    if (response.code !== 200) utils.showToasts('error' , data.message);
                });
            } else utils.showToasts('error' , data.message);
        }
    }
</script>

<template>
    <div>
        <el-footer class="content-footer" v-if="applicationStore.loginStatus">
            <div class="input-box">
                <el-input autosize type="textarea" class="chat-input" v-model="applicationStore.chantInput" placeholder="请在此输入聊天内容，按Ctrl+Enter发送..." clearable maxlength="100" show-word-limit @keydown="handleKeydown"/>
                <el-button class="chat-button" type="primary" plain @click="sendChatMessageV2">{{ applicationStore.sendst ? '正在发送中' : '发送消息' }}</el-button>
            </div>
            
            <el-dropdown placement="top" v-if="!applicationStore.isDeviceMobile">
                <el-button class="more-button" type="primary" plain>
                    <svg t="1708769523336" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5067" width="20" height="20"><path d="M925.696 384q19.456 0 37.376 7.68t30.72 20.48 20.48 30.72 7.68 37.376q0 20.48-7.68 37.888t-20.48 30.208-30.72 20.48-37.376 7.68l-287.744 0 0 287.744q0 20.48-7.68 37.888t-20.48 30.208-30.72 20.48-37.376 7.68q-20.48 0-37.888-7.68t-30.208-20.48-20.48-30.208-7.68-37.888l0-287.744-287.744 0q-20.48 0-37.888-7.68t-30.208-20.48-20.48-30.208-7.68-37.888q0-19.456 7.68-37.376t20.48-30.72 30.208-20.48 37.888-7.68l287.744 0 0-287.744q0-19.456 7.68-37.376t20.48-30.72 30.208-20.48 37.888-7.68q39.936 0 68.096 28.16t28.16 68.096l0 287.744 287.744 0z" p-id="5068" fill="#ffffff"></path></svg>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="onelDialogStore.setEmojeListCenter(true)">键入表情</el-dropdown-item>
                    <el-dropdown-item @click="onelDialogStore.setUploadFileCenter(true)">发送文件</el-dropdown-item>
                    <el-upload ref="uploadRef" class="upload-avatar" :action="config.serverAdress + config.serverApi.uploadImages" @success="uploadFileSuccess" @error="utils.uploadFileError" :headers="{
                        uid: applicationStore.userInfo.uid,
                        gid: applicationStore.groupInfo.gid,
                        token: utils.getClientToken()
                    }">
                        <template #trigger>
                            <el-dropdown-item>发送图片</el-dropdown-item>
                        </template>
                    </el-upload>
                  </el-dropdown-menu>
                </template>
            </el-dropdown>
        </el-footer>
        <el-footer class="content-footer-no" v-else>
            <p class="no-text">游客朋友你好，请 <span class="login" @click="utils.openUserLoginForms">登录</span> 后参与聊天</p>
        </el-footer>
    </div>
</template>

<style>
    @import url("./styles/ChatFootBox.css");

    ul.el-upload-list {
        display: none;
    }
</style>
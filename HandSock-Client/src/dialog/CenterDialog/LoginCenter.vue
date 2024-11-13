<script setup>
    import { reactive, ref } from 'vue'
    import utils from '@/scripts/utils'
    import { saveLocalStorage } from '@/scripts/storageUtils'
    import { sendSocketEmit, toggleConnectStatus, resetOnlineUsers } from "@/socket/socketClient"

    const activeTab = ref('login');
    const applicationStore = utils.useApplicationStore();
    const loginForm = reactive({ username: '', password: '' });
    const registerForm = reactive({ username: '', password: '' });

    const validateInput = (username, password) => {
        const textRegex = "^[a-zA-Z0-9]+$";
        if (!username.match(textRegex)) {
            utils.showToasts('error', '账号格式不合规');
            return;
        }
        if (!password.match(textRegex)) {
            utils.showToasts('error', '密码格式不合规');
            return;
        }
        if (username.length < 5 || username.length > 20) {
            utils.showToasts('error', '账号长度不合规');
            return;
        }
        if (password.length < 5 || password.length > 20) {
            utils.showToasts('error', '密码长度不合规');
            return
        }
        return true;
    };

    const handleAuth = async (mode, form) => {
        if (mode === 1) {
            if (loginForm.username === '' || loginForm.password === '') return utils.showToasts('error', '输入格式不合规！');
        } else if (mode === 2) if (!validateInput(form.username, form.password)) return;

        await sendSocketEmit(`[USER:${mode === 1 ? 'LOGIN' : 'REGISTER'}]`, form, async (response) => {
            if (response.code !== 200) {
                utils.showToasts('error', response.message);
            } else {
                applicationStore.setLoginFormStatus(false);
                utils.showToasts('success', response.message);
                await saveLocalStorage(["handsock_uid", "handsock_token", "handsock_username"], [response.data.userinfo.uid, response.data.token, response.data.userinfo.username]).then(async () => {
                    setTimeout(async () => {
                        await toggleConnectStatus([]);
                        await resetOnlineUsers(1);
                    }, 300);
                    applicationStore.userInfo = response.data.userinfo;
                });
            }
        });
    };

    const formButtonClick = async (mode) => {
        await handleAuth(mode, mode === 1 ? loginForm : registerForm);
    };
</script>

<template>
    <el-dialog class="login-form handsock-dialog" v-model="applicationStore.loginFormStatus" align-center :width="applicationStore.isDeviceMobile ? '95%' : '450px'" style="padding: 0;">
        <el-tabs v-model="activeTab" class="login-tabs">
            <el-tab-pane label="登录" name="login">
                <div class="form-box">
                    <el-form :model="loginForm">
                        <el-form-item label="账号" prop="username">
                            <el-input v-model="loginForm.username" clearable/>
                        </el-form-item>

                        <el-form-item label="密码" prop="password">
                            <el-input v-model="loginForm.password" type="password" clearable show-password/>
                        </el-form-item>
                        <el-button class="login-btn" type="primary" @click="formButtonClick(1)">登录</el-button>
                    </el-form>
                </div>
            </el-tab-pane>
            <el-tab-pane label="注册" name="register">
                <div class="form-box">
                    <el-form :model="registerForm">
                        <el-form-item label="账号" prop="username">
                            <el-input v-model="registerForm.username" clearable/>
                        </el-form-item>

                        <el-form-item label="密码" prop="password">
                            <el-input v-model="registerForm.password" type="password" clearable show-password/>
                        </el-form-item>
                        <el-button class="login-btn" type="primary" @click="formButtonClick(2)">注册</el-button>
                    </el-form>
                </div>
            </el-tab-pane>
        </el-tabs>
    </el-dialog>
</template>

<style>
    @import url("./styles/LoginCenter.css");
</style>
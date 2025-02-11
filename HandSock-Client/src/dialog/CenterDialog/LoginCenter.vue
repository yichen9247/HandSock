<!--
 * @Description: Login and registration dialog component with form validation
 * @Author: Hua
 * @Date: 2024-11-25
 * @Features:
 *   - User login and registration forms
 *   - Input validation with regex
 *   - Persistent storage of auth tokens
 *   - Responsive dialog width
 *   - Tab-based form switching
-->

<script setup lang="ts">
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import { saveLocalStorage } from '@/scripts/storageUtils'
    import { restfulType, loginFormType } from '../../../types'
    import { sendSocketEmit, toggleConnectStatus, resetOnlineUsers } from "@/socket/socketClient"

    const activeTab = ref('login');
    const applicationStore = utils.useApplicationStore();
    const loginForm = reactive({ username: '', password: '' });
    const registerForm = reactive({ username: '', password: '' });

    /**
     * Validate username and password format
     * @param {string} username - Username to validate
     * @param {string} password - Password to validate
     * @returns {boolean} Whether validation passed
     */
    const validateInput = (username: string, password: string): boolean => {
        const textRegex = /^[a-zA-Z0-9]+$/;
        const lengthRange = { min: 5, max: 20 };

        if (!username || !password) {
            utils.showToasts('error', '输入格式不合规');
            return false;
        }

        if (!textRegex.test(username)) {
            utils.showToasts('error', '账号格式不合规');
            return false;
        }
        if (!textRegex.test(password)) {
            utils.showToasts('error', '密码格式不合规');
            return false;
        }
        if (username.length < lengthRange.min || username.length > lengthRange.max) {
            utils.showToasts('error', '账号长度不合规');
            return false;
        }
        if (password.length < lengthRange.min || password.length > lengthRange.max) {
            utils.showToasts('error', '密码长度不合规');
            return false;
        }
        return true;
    }

    /**
     * Handle login/register authentication
     * @param {number} mode - 1 for login, 2 for register
     * @param {Object} form - Form data containing username and password
     */
    const handleAuth = async (mode: number, form: loginFormType) => {
        if (!validateInput(form.username, form.password)) return;
        await sendSocketEmit(mode === 1 ? socket.send.User.UserLogin : socket.send.User.UserRegister, form, async (response: restfulType) => {
            if (response.code !== 200) return utils.showToasts('error', response.message);
            applicationStore.setLoginFormStatus(false);
            utils.showToasts('success', response.message);
            
            const { userinfo, token } = response.data;
            await saveLocalStorage([
                "handsock_uid",
                "handsock_token", 
                "handsock_username"
            ], [
                userinfo.uid,
                token,
                userinfo.username
            ]).then(async () => {
                setTimeout(async (): Promise<void> => {
                    await toggleConnectStatus([]);
                    await resetOnlineUsers(1);
                }, 300);
                applicationStore.userInfo = userinfo;
            });
        });
    }

    const formButtonClick = async (mode: number): Promise<void> => {
        await handleAuth(mode, mode === 1 ? loginForm : registerForm);
    }
</script>

<template>
    <el-dialog 
        class="login-form handsock-dialog" 
        v-model="applicationStore.loginFormStatus" 
        align-center 
        :width="applicationStore.isDeviceMobile ? '95%' : '450px'"
        style="padding: 0;"
    >
        <el-tabs v-model="activeTab" class="login-tabs">
            <el-tab-pane label="登录" name="login">
                <div class="form-box">
                    <el-form :model="loginForm" @keydown.enter="formButtonClick(1)">
                        <el-form-item label="账号" prop="username">
                            <el-input v-model="loginForm.username" clearable/>
                        </el-form-item>

                        <el-form-item label="密码" prop="password">
                            <el-input 
                                v-model="loginForm.password" 
                                type="password" 
                                clearable 
                                show-password
                            />
                        </el-form-item>
                        <el-button 
                            class="login-btn" 
                            type="primary" 
                            @click="formButtonClick(1)"
                        >
                            登录
                        </el-button>
                    </el-form>
                </div>
            </el-tab-pane>

            <el-tab-pane label="注册" name="register">
                <div class="form-box">
                    <el-form :model="registerForm" @keydown.enter="formButtonClick(2)">
                        <el-form-item label="账号" prop="username">
                            <el-input v-model="registerForm.username" clearable/>
                        </el-form-item>

                        <el-form-item label="密码" prop="password">
                            <el-input 
                                v-model="registerForm.password" 
                                type="password" 
                                clearable 
                                show-password
                            />
                        </el-form-item>
                        <el-button 
                            class="login-btn" 
                            type="primary" 
                            @click="formButtonClick(2)"
                        >
                            注册
                        </el-button>
                    </el-form>
                </div>
            </el-tab-pane>
        </el-tabs>
    </el-dialog>
</template>

<style lang="less">
    @import url("./styles/LoginCenter.less");
    .verify-model {
        z-index: 999999999!important;

        .vue-auth-box_ {
            top: 50%;
        }
    }
</style>
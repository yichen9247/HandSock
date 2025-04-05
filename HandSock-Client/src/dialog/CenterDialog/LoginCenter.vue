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
    import QRCode from 'qrcode'
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import HandUtils from '@/scripts/HandUtils'
    import { restfulType, loginFormType } from '../../../types'

    let intervalTimer: any;
    const qrcode = ref(null);
    const exStatus = ref(false);
    const activeTab = ref('login');
    const qrcodeLoading = ref(false);
    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    const loginForm = reactive({ 
        username: localStorage.getItem("handsock_username") ? localStorage.getItem("handsock_username") : "", 
        password: localStorage.getItem("handsock_password") ? localStorage.getItem("handsock_password") : ""
    });
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
            utils.showToasts('error', '输入不合规');
            return false;
        }

        if (!textRegex.test(username)) {
            utils.showToasts('error', '账号不合规');
            return false;
        }
        if (!textRegex.test(password)) {
            utils.showToasts('error', '密码不合规');
            return false;
        }
        if (username.length < lengthRange.min || username.length > lengthRange.max) {
            utils.showToasts('error', '长度不合规');
            return false;
        }
        if (password.length < lengthRange.min || password.length > lengthRange.max) {
            utils.showToasts('error', '长度不合规');
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
        await HandUtils.sendClientSocketEmit({
            data: form,
            event: mode === 1 ? socket.send.User.UserLogin : socket.send.User.UserRegister,
            callback: async (response: restfulType) => {
                HandUtils.handleUserLogin(response, loginForm.password)
            }
        });
    }

    const formButtonClick = async (mode: number): Promise<void> => {
        await handleAuth(mode, mode === 1 ? loginForm : registerForm);
    }

    const tabChange = async (key: string): Promise<void> => {
        if (key === 'scan') {
            onScanQrcodeTabShow();
        } else {
            qrcode.value = null;
            clearInterval(intervalTimer);
        }
    }

    const onScanQrcodeTabShow = async (): Promise<void> => {
        await applicationStore.socketIo.emit(socket.send.User.UserScanLogin, loginForm, async (response: restfulType) => {
            exStatus.value = false;
            qrcodeLoading.value = true;
            if (response.code !== 200) return utils.showToasts('error', response.message);
                setTimeout(() => {
                    qrcodeLoading.value = false;
                    QRCode.toDataURL(JSON.stringify(response.data), {
                        errorCorrectionLevel: 'M'
                    }).then(url => {
                        qrcode.value = url;
                    }).catch(err => {
                        utils.showToasts('error', "生成二维码失败");
                    });
                }, 500);
                intervalTimer = setInterval(async () => {
                    applicationStore.socketIo.emit(socket.send.User.UserScanLoginStatus, {
                        qid: response.data.qid
                    }, async (statusResponse: restfulType) => {
                        if (statusResponse.code === 200) {
                            HandUtils.handleUserLogin(statusResponse, loginForm.password)
                            clearInterval(intervalTimer);
                        } else 
                        if (statusResponse.code === 401) {
                            exStatus.value = true;
                            clearInterval(intervalTimer);
                            utils.showToasts('error', statusResponse.message);
                        }
                    });
		        }, 500);
            }
        )
    }
    
    onUnmounted(() => {
        qrcode.value = null;
        exStatus.value = false;
        clearInterval(intervalTimer);
    });
</script>

<template>
    <el-dialog 
        class="login-form handsock-dialog" 
        v-model="onelDialogStore.userLoginCenter" 
        align-center 
        :width="applicationStore.isDeviceMobile ? '95%' : '450px'"
        style="padding: 0;"
    >
        <el-tabs v-model="activeTab" class="login-tabs" @tab-change="tabChange">
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
                            立即登录
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
                            立即注册
                        </el-button>
                    </el-form>
                </div>
            </el-tab-pane>

            <el-tab-pane label="扫码" name="scan">
                <div class="form-box">
                    <div class="app-box">
                        <div class="qrcode-box" 
                            v-loading="qrcodeLoading" 
                            element-loading-text="正在请求中"
                            element-loading-background="rgba(255, 255, 255, 0.95)"
                        >
                            <img v-if="qrcode" class="qrcode" :src="qrcode" alt="扫码登录" draggable="false">
                            <div class="expire-box" v-if="exStatus" @click="onScanQrcodeTabShow">
                                <svg t="1742615455835" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3521" width="48" height="48"><path d="M938.336973 255.26894c-16.685369-6.020494-35.090879 2.752226-40.939358 19.437594l-24.770032 69.493701c-29.070385-65.537376-74.998152-123.162103-133.48295-166.337645-185.947253-137.611288-450.848984-100.112212-590.180413 83.942886C81.534688 350.908785 52.980346 460.653788 68.805644 570.742819c15.825298 110.605073 74.48211 208.481102 164.789518 275.394591 75.686209 55.904586 164.273476 83.082815 252.172686 83.082815 128.494541 0 255.26894-57.624727 338.007727-166.853687 36.639006-48.335965 61.581052-102.348396 74.48211-160.833193 3.78431-17.373425-7.224593-34.402822-24.426004-38.187133-17.201411-3.78431-34.402822 7.052579-38.187133 24.426004-10.836889 49.36805-31.994625 95.123803-62.957164 135.891147-118.173694 156.016798-342.996136 187.839409-500.90509 70.869814-76.546279-56.592642-126.086343-139.33143-139.503444-232.907106-13.417101-93.059634 10.664875-185.775239 67.77356-261.11742C318.05409 144.491853 542.704519 112.497228 700.785486 229.466823c57.280699 42.315471 100.112212 100.972283 123.334117 167.197715l-110.261045-43.003528c-16.513355-6.364522-35.090879 1.720141-41.627415 18.233496-6.536536 16.513355 1.720141 35.090879 18.233496 41.627415l162.38132 63.473207c3.78431 1.548127 7.740635 2.236183 11.69696 2.236183 0.516042 0 1.032085-0.172014 1.548127-0.172014 1.204099 0.172014 2.408198 0.688056 3.612296 0.688056 13.245087 0 25.630102-8.256677 30.274483-21.32975l57.796741-161.693264C963.623047 279.694944 955.022342 261.289434 938.336973 255.26894z" fill="var(--dominColor)" p-id="3522"></path></svg>
                                <span class="expire-text">二维码已过期</span>
                            </div>
                        </div>
                        <p class="qrcode-description">请在
                            <el-link 
                                type="primary" 
                                target="_blank"
                            >
                                30秒内
                            </el-link>完成登录
                        </p>
                    </div>
                </div>
            </el-tab-pane>
        </el-tabs>
    </el-dialog>
</template>

<style lang="less">
    @import url("./styles/LoginCenter.less");
</style>
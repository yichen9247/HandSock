<script setup>
    import { ref } from 'vue'
    import utils from '@/scripts/utils'
    
    const active = ref(0);
    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <div id="m-settings">
        <van-tabs v-model:active="active" animated>
            <van-tab title="个人设置">
                <div class="tab-b">
                    <UserSetting/>
                </div>
            </van-tab>
            <van-tab title="主题设置">
                <div class="tab-b">
                    <ThemeSetting/>
                </div>
            </van-tab>
            <van-tab title="聊天设置">
                <div class="tab-b">
                    <ChatSetting/>
                </div>
            </van-tab>
            <van-tab title="管理后台">
                <div class="tab-b">
                    <AdminPage v-if="applicationStore.userList.find(item => item.uid === applicationStore.userInfo.uid).isAdmin"/>
                    <el-result icon="info" title="无权查看" v-else>
                        <template #sub-title>
                            <p>此功能仅管理员可用</p>
                        </template>
                    </el-result>
                </div>
            </van-tab>
            <van-tab title="隐私政策">
                <div class="tab-b">
                    <PrivacyPage/>
                </div>
            </van-tab>
            <van-tab title="关于项目">
                <div class="tab-b">
                    <el-text class="mx-1">
                        <p>HandSock 是一款有趣的聊天应用，基于 Mybatis-Plus，Springboot, Vue3 和 Socket.io，Redis 等技术开发</p>
                        <br/>
                        <p style="display: flex; align-items: center; flex-wrap: wrap;">项目作者：<el-link type="primary" href="https://github.com/yichen9247" target="_blank">yichen9247（Hua）</el-link></p>
                        <br/>
                        <p style="display: flex; align-items: center; flex-wrap: wrap;">项目地址：<el-link type="primary" href="https://github.com/yichen9247/HandSock" target="_blank">https://github.com/yichen9247/HandSock</el-link></p>
                    </el-text>
                </div>
            </van-tab>
        </van-tabs>
    </div>
</template>

<style>
    @import url("./styles/MobileSettings.css");
</style>
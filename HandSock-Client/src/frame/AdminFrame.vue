<script setup>
    import MsgManner from "./items/MsgManner.vue"
    import ChanManner from "./items/ChanManner.vue"
    import UserManner from "./items/UserManner.vue"
    import DashManner from "./items/DashManner.vue"
    import SystemLogs from "./items/SystemLogs.vue"
    import ReportManner from "./items/ReportManner.vue"
    import UploadManner from "./items/UploadManner.vue"
    import SystemManner from "./items/SystemManner.vue"

    const adminTabList = reactive([{
        key: 'dash',
        name: '系统概览',
        component: markRaw(DashManner)
    },{
        key: 'user',
        name: '用户管理',
        component: markRaw(UserManner)
    },{
        key: 'msg',
        name: '消息管理',
        component: markRaw(MsgManner)
    },{
        key: 'chan',
        name: '频道管理',
        component: markRaw(ChanManner)
    }, {
        key: 'file',
        name: '上传管理',
        component: markRaw(UploadManner)
    }, {
        key: 'report',
        name: '举报管理',
        component: markRaw(ReportManner)
    }, {
        key: 'logs',
        name: '系统日志',
        component: markRaw(SystemLogs)
    }, {
        key: 'system',
        name: '系统管理',
        component: markRaw(SystemManner)
    }]);

    const settingIndex = ref(adminTabList[0].key);
    const handleSelect = (key) => settingIndex.value = key;
</script>

<template>
    <div class="admin-frame">
        <div class="frame-header">
            <span class="header-title">管理后台</span>
        </div>
        <div class="frame-content">
            <div class="frame-sidebar">
                <el-menu default-active="dash" class="el-menu-vertical-admin" @select="handleSelect">
                    <el-menu-item v-for="(item, index) in adminTabList" :key="index" :index="item.key">
                        <el-icon></el-icon>
                        <span>{{ item.name }}</span>
                    </el-menu-item>
                </el-menu>
            </div>

            <div class="admin-item-content item-content">
                <component :is="adminTabList.find(item => item.key === settingIndex).component"/>
            </div>
        </div>
    </div>
</template>

<style lang="less">
    @import url("./styles/AdminFrame.less");
</style>
<script setup>
    import MsgManner from "./items/MsgManner.vue"
    import ChanManner from "./items/ChanManner.vue"
    import UserManner from "./items/UserManner.vue"
    import DashManner from "./items/DashManner.vue"
    import SystemLogs from "./items/SystemLogs.vue"
    import PostManner from "./items/PostManner.vue"
    import ReportManner from "./items/ReportManner.vue"
    import BannerManner from "./items/BannerManner.vue"
    import NoticeManner from "./items/NoticeManner.vue"
    import UploadManner from "./items/UploadManner.vue"
    import SystemManner from "./items/SystemManner.vue"
    import CommentManner from "./items/CommentManner.vue"

    const adminTabList = reactive([{
        key: 'dash',
        name: '系统概览',
        component: markRaw(DashManner)
    }, {
        key: 'logs',
        name: '系统日志',
        component: markRaw(SystemLogs)
    }, {
        key: 'system',
        name: '系统管理',
        component: markRaw(SystemManner)
    }, {
        key: 'notice',
        name: '公告管理',
        component: markRaw(NoticeManner)
    }, {
        key: 'user',
        name: '用户管理',
        component: markRaw(UserManner)
    }, {
        key: 'chan',
        name: '频道管理',
        component: markRaw(ChanManner)
    }, {
        key: 'banner',
        name: '轮播管理',
        component: markRaw(BannerManner)
    }, {
        key: 'report',
        name: '举报管理',
        component: markRaw(ReportManner)
    }, {
        key: 'msg',
        name: '消息管理',
        component: markRaw(MsgManner)
    }, {
        key: 'file',
        name: '上传管理',
        component: markRaw(UploadManner)
    }, {
        key: 'post',
        name: '帖子管理',
        component: markRaw(PostManner)
    }, {
        key: 'comment',
        name: '评论管理',
        component: markRaw(CommentManner)
    }])

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
                    <el-menu-item index="dash">
                        <el-icon/>
                        <span>系统概览</span>
                    </el-menu-item>

                    <el-menu-item index="user">
                        <el-icon/>
                        <span>用户管理</span>
                    </el-menu-item>

                    <el-menu-item index="chan">
                        <el-icon/>
                        <span>频道管理</span>
                    </el-menu-item>

                    <el-menu-item index="banner">
                        <el-icon/>
                        <span>轮播管理</span>
                    </el-menu-item>

                    <el-menu-item index="notice">
                        <el-icon/>
                        <span>公告管理</span>
                    </el-menu-item>

                    <el-menu-item index="report">
                        <el-icon/>
                        <span>举报管理</span>
                    </el-menu-item>

                    <el-sub-menu index="content">
                        <template #title>
                            <el-icon/>
                            <span>内容管理</span>
                        </template>

                        <el-menu-item v-for="(item, index) in adminTabList.slice(8)" :key="index" :index="item.key">
                            <span>{{ item.name }}</span>
                        </el-menu-item>
                    </el-sub-menu>

                    <el-menu-item index="logs">
                        <el-icon/>
                        <span>系统日志</span>
                    </el-menu-item>

                    <el-menu-item index="system">
                        <el-icon/>
                        <span>系统管理</span>
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
<script setup lang="ts">
    import socket from "@/socket/socket"
    import release from "@/assets/image/app-release.png"
</script>

<template>
    <div class="about-page">
        <el-text class="mx-1" style="text-align: justify;">
            <p class="description" style="line-height: 26px;">{{ socket.application.description }}</p>
            <br/>
            <p style="display: flex; align-items: center; flex-wrap: wrap;">
                安卓下载：
                <el-link 
                    type="primary" 
                    target="_blank"
                    :href="socket.application.appDownload" 
                >
                    点击下载
                </el-link>
            </p>
            <br/>
            <p style="display: flex; align-items: center; flex-wrap: wrap;">
                项目作者：
                <el-link 
                    type="primary" 
                    target="_blank"
                    :href="socket.application.author" 
                >
                    {{ socket.application.authorName }}
                </el-link>
            </p>
            <br/>
            <p style="display: flex; align-items: center; flex-wrap: wrap;">
                项目地址：
                <el-link 
                    type="primary" 
                    target="_blank"
                    :href="socket.application.github" 
                >
                    {{ socket.application.github }}
                </el-link>
            </p>
            <p style="flex: 1; display: flex; justify-content: center;">
                <img class="qrcode" :src="release" alt="安卓下载" draggable="false">
            </p>
        </el-text>
    </div>
</template>

<style lang="less">
    div.about-page {
        p {
            text-align: justify;
            word-break: break-all;
        }

        img.qrcode {
            width: 200px;
            height: 200px;
            margin-top: 35px;
        }
    }
</style>
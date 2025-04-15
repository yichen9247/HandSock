<script setup lang="ts">
    import axios from 'axios'
    import { Reactive } from "vue"
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import HandUtils from '@/scripts/HandUtils'
    import { Plus } from '@element-plus/icons-vue'
    import ForumContent from '@/forum/ForumContent.vue'
    import type { UploadProps, UploadUserFile } from 'element-plus'
    import {addPostFormType, restfulType, uploadStatusType} from '../../../types'

    const formData: Reactive<addPostFormType> = reactive({
        title: "",
        content: "",
        image: [] as string[]
    });

    const uploadStep = ref(0);
    const uploadStatus = ref(0);
    const postImageList = ref<UploadUserFile[]>([]);
    const applicationStore = utils.useApplicationStore();

    const handleRemove: UploadProps['onRemove'] = () => {}
    const handlePictureCardPreview: UploadProps['onPreview'] = async (): Promise<void> => {
        await utils.showToasts('warning', "暂不支持预览");
    }

    const onButtonClick = async () => {
        if (!formData.title.trim() || formData.content.trim() === "") return utils.showToasts('warning', '请填写完整信息');
        try {
            if (postImageList.value.length > 0) uploadStatus.value = 1;
            for (let index = 0; index < postImageList.value.length; index++) {
                uploadStep.value = index + 1;
                const file = postImageList.value[index];
                await axios.post(socket.server.config.serverUrl + socket.server.uploadImages, { file: file.raw}, {
                    headers: {
                        "uid": applicationStore.userInfo.uid,
                        "gid": applicationStore.groupInfo.gid,
                        "token": HandUtils.getClientToken(),
                        "content-type": "multipart/form-data",
                    },
                }).then(response => {
                    const data: restfulType<uploadStatusType> = response.data
                    if (data.code === 200) {
                        formData.image.push(data.data.path);
                    } else utils.showToasts('error', data.message);
                }).catch(error => {
                    utils.showToasts('error', error)
                });
                await new Promise((resolve) => setTimeout(resolve, 200));
            }
            await addForumPost();
        } catch (error) {
            await utils.showToasts('error', '上传失败，请重试');
        } finally {
            uploadStatus.value = 2;
        }
    }

    const addForumPost = async (): Promise<void> => {
        await HandUtils.sendClientSocketEmit({
            event: socket.send.Forum.AddForumPost,
            data: {
                title: formData.title,
                image: JSON.stringify(formData.image),
                content: formData.content,
            },
            callback: async (response: restfulType<any>): Promise<void> => {
                if (response.code === 200) {
                    await utils.showToasts('success', response.message);
                } else await utils.showToasts('error', response.message);
            }
        }).finally(() => {
            HandUtils.openCustomSwalDialog(ForumContent);
        });
    }
</script>

<template>
    <div class="add-post">
        <el-form class="add-post-form" label-width="auto">
            <div class="flex-form">
                <el-form-item label="帖子标题">
                    <el-input v-model="formData.title" placeholder="请输入帖子标题" style="height: 40px;" maxlength="15" show-word-limit/>
                </el-form-item>

                <el-form-item label="帖子内容" style="margin-top: auto; margin-bottom: 0;">
                    <el-input type="textarea" :rows="5" v-model="formData.content" placeholder="请输入帖子内容" maxlength="1000" show-word-limit/>
                </el-form-item>
            </div>
            <div class="flex-form">
                <el-form-item label="帖子图片" class="image-upload">
                    <el-upload style="width: 100%;" v-model:file-list="postImageList" list-type="picture-card" accept="image/*" ref="uploadRef"
                        :limit="4" :on-remove="handleRemove" :auto-upload="false" :on-preview="handlePictureCardPreview"
                    >
                        <el-icon><Plus /></el-icon>
                    </el-upload>
                </el-form-item>
            </div>
        </el-form>
        <el-button type="primary" size="large" style="width: 100%; height: 50px; margin-top: 35px; font-size: 18px;" @click="onButtonClick" :loading="uploadStatus === 1">{{ uploadStatus === 1 ? `正在上传第 ${uploadStep} / ${postImageList.length} 张图片` : "发布帖子" }}</el-button>
    </div>
</template>

<style lang="less">
    @import url("./styles/AddPostCenter.less");
</style>
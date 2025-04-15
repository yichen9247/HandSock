<script setup lang="ts">
    import { Reactive } from "vue"
    import utils from "@/scripts/utils"
    import HandUtils from "@/scripts/HandUtils"
    import RequestUtils from "@/scripts/RequestUtils"
    import { addPostCommentType, forumCommentType } from "../../types"

    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const replyPost: Ref<boolean> = ref(false);
    const commentList: Reactive<Array<forumCommentType>> = reactive([]);

    const config = defineProps({
        postId: { type: Number, required: true }
    });

    const formData: Reactive<addPostCommentType> = reactive({
        content: "",
        parent: null,
        pid: config.postId
    });

    const replySubComment = (id: number): void => {
        const isSameParent = id === formData.parent;
        replyPost.value = !(replyPost.value && isSameParent);
        formData.parent = id;
    };

    const getCommentList = async (): Promise<void> => {
        if (!config.postId) return;
        await RequestUtils.getForumCommentList(config.postId, pages.value, 10).then((data) => {
            total.value = data.total;
            commentList.splice(0, commentList.length, ...data.items);
        });
    }

    const sendAddComment = async (): Promise<void> => {
        if (formData.content.trim() === "" || !formData.pid) return utils.showToasts('warning', '请填写完整信息');
        replyPost.value = false;
        await RequestUtils.addForumComment(formData).then(async (): Promise<void> => {
            await utils.showToasts('success', "发送成功");
            await getCommentList()
        }).finally(() => {
            formData.content = "";
            formData.parent = null;
        });
    }

    const currentChange = async (page: number): Promise<void> => {
        pages.value = page;
        await getCommentList();
    }
    onMounted(async (): Promise<void> => await getCommentList());
</script>

<template>
    <div class="post-comment">
        <div class="comment-head">
            <span class="head-text">评论列表（{{ commentList.length }}）</span>
            <el-button type="primary" link @click="replySubComment(null)">评论</el-button>
        </div>
        <div class="reply-box" v-if="replyPost && !formData.parent">
            <el-form>
                <el-form-item>
                    <el-input v-model="formData.content" type="text" maxlength="100" placeholder="请输入评论内容，请勿灌水"/>
                    <el-button type="primary" @click="sendAddComment">评论</el-button>
                </el-form-item>
            </el-form>
        </div>
        <ul class="comment-list" v-for="(item, index) in commentList" :key="index">
            <li class="comment-list-item">
                <div class="item-content">
                    <div class="content-head">
                        <el-avatar class="avatar" :size="28" :src="HandUtils.getUserAvatarByPath(item.user.avatar)"/>
                        <div class="comment-basic">
                            <div class="basic-info">
                                <span class="username">{{ item.user.nick }}</span>
                                <span class="datetime">{{ item.time }}</span>
                            </div>
                            <el-button type="primary" link @click="replySubComment(item.cid)">回复</el-button>
                        </div>
                    </div>
                    <div class="content-body">
                        <p class="comment-content">{{ item.content }}</p>
                        <div class="reply-box" v-if="replyPost && formData.parent === item.cid">
                            <el-form>
                                <el-form-item>
                                    <el-input v-model="formData.content" type="text" maxlength="100" placeholder="请输入评论内容，请勿灌水"/>
                                    <el-button type="primary" @click="sendAddComment">回复</el-button>
                                </el-form-item>
                            </el-form>
                        </div>
                        <div class="reply-content" v-for="(subItem, subIndex) in item.children" :key="subIndex">
                            <div class="basic-info">
                                <span class="username">{{ subItem.user.nick }}</span>
                                <span class="datetime">{{ subItem.time }}</span>
                            </div>
                            {{ subItem.content }}
                        </div>
                    </div>
                </div>
            </li>
        </ul>
        <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange"/>
    </div>
</template>

<style lang="less">
    @import url("./styles/ForumComment.less");
</style>
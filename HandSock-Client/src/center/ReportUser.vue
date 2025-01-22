<script setup lang="ts">
    import { Reactive } from 'vue'
    import Swal from 'sweetalert2'
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import { sendSocketEmit } from '@/socket/socketClient'
    import { restfulType, reportFormType } from '../../types'

    const formData: Reactive<reportFormType> = reactive({
        sid: "",
        reason: "",
        reported_id: null
    });

    const onelDialogStore = utils.useOnelDialogStore();
    const applicationStore = utils.useApplicationStore();

    const onReportChange = (sid: string): void => {
        const messageInfo = utils.queryMessageInfo(sid);
        formData.reported_id = messageInfo.uid;
    }

    const reversedMessages = computed((): any => applicationStore.messageList.slice().reverse());

    const onReportSubmit = async (): Promise<void> => {
        if (!formData.reported_id || formData.sid === "" || formData.reason === "") return utils.showToasts('warning', '请填写完整信息');
        onelDialogStore.setReportUserCenter(false);
        await sendSocketEmit(socket.send.ReportUser, {
            sid: formData.sid,
            reason: formData.reason,
            reported_id: formData.reported_id
        }, async (response: restfulType): Promise<void> => {
            if (response.code === 200) {
                Swal.fire({
                    icon: "success",
                    title: "举报成功",
                    showConfirmButton: false,
                    text: "感谢你的举报，我们将认真审核"
                });
            } else await utils.showToasts('error', response.message);
        });
    }
</script>

<template>
    <div class="handsock-report-user">
        <el-form class="report-form">
            <el-form-item label="举报用户">
                <el-input v-model="formData.reported_id" placeholder="将根据聊天记录自动选择" disabled/>
            </el-form-item>

            <el-form-item label="举报记录">
                <el-select v-model="formData.sid" placeholder="请选择举报记录" @change="onReportChange" style="width: 100%; max-width: 100%;">
                    <el-option style="width: 468px; max-width: 468px;" v-for="(item, index) in reversedMessages" :key="index" :label="`${utils.queryUserInfo(item.uid).nick}：${ utils.getMessageType(item.type, item.content) }`" :value="item.sid"/>
                </el-select>
            </el-form-item>

            <el-form-item label="举报理由">
                <el-input type="textarea" v-model="formData.reason" placeholder="请输入举报理由" />
            </el-form-item>
            <el-button type="primary" style="width: 100%; height: 40px; font-size: 1rem; margin-top: 10px;" @click="onReportSubmit">确认举报</el-button>
        </el-form>
    </div>
</template>

<style lang="less">
    @import url("./styles/ReportUser.less");
</style>
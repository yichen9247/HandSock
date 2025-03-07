<script setup lang="ts">
    import { Reactive } from 'vue'
    import utils from '@/scripts/utils'
    import socket from '@/socket/socket'
    import HandUtils from '@/scripts/HandUtils'
    import { Plus, Refresh } from '@element-plus/icons-vue'
    import { adminBannerFormType, restfulType } from '../../../types'

    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const formModel: Ref<number> = ref(0);
    const loading: Ref<boolean> = ref(false);
    const rightDrawer: Ref<boolean> = ref(false);
    const tableData: Reactive<Array<any>> = reactive([]);

    const applicationStore = utils.useApplicationStore();
    const showToast = async (type: string, message: string): Promise<void> => await utils.showToasts(type, message);

    const formData: Reactive<adminBannerFormType> = reactive({
        bid: null, name: "", href: "", image: ""
    });

    onMounted(async () => await getBannerList());

    const getBannerList = async (): Promise<void> => {
        loading.value = true;
        applicationStore.socketIo.emit(socket.send.Admin.Get.GetAdminBannerList, {
            page: pages.value,
            limit: 10,
        }, async (response: restfulType): Promise<void> => {
            if (response.code !== 200) {
                utils.showToasts('error', response.message);
            } else {
                total.value = response.data.total;
                tableData.splice(0, tableData.length, ...response.data.items);

                if (tableData.length === 0 && response.data.total > 0) {
                    pages.value = pages.value - 1;
                    await getBannerList();
                }
            }
            loading.value = false;
        });
    }

    const currentChange = async (page: number): Promise<void> => {
        pages.value = page;
        await getBannerList();
    }

    const createBanner = (): void => {
        formModel.value = 1;
        formData.href = "";
        formData.name = "";
        formData.image = "";
        rightDrawer.value = true;
    }

    const updateBanner = (row: any): void => {
        formModel.value = 0;
        formData.bid = row.bid;
        formData.name = row.name,
        formData.href = row.href;
        rightDrawer.value = true;
        formData.image = row.image;
    }

    const sendSocketRequest = async (action: any, data: any, callback: any): Promise<void> => {
        await HandUtils.checkClientLoginStatus(async (): Promise<void> => {
            await HandUtils.sendClientSocketEmit({
                data: data,
                event: action,
                callback: async (response: restfulType) => {
                    if (response.code !== 200) {
                        showToast('error', response.message);
                    } else {
                        await getBannerList();
                        showToast('success', response.message);
                    }
                    callback && await callback(response);
                }
            });
        });
    };

    const handleDeleteBanner = (bid: number): void => {
        ElMessageBox.confirm(`是否确认删除编号为 ${bid} 的轮播`, '删除轮播', {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
        }).then(async (): Promise<void> => await sendSocketRequest(socket.send.Admin.Del.DelAdminBanner, { bid }, (): void => {}));
    };

    const refreshList = async (): Promise<void> => {
        await getBannerList();
        await showToast('success', "刷新列表成功");
    }

    const sendCreateBanner = async (): Promise<void> => {
        if (formData.name === null || formData.href === "" || formData.image === "") return showToast('warning', "必填项为空");
        await sendSocketRequest(formModel.value ? socket.send.Admin.Add.AddAdminBanner : socket.send.Admin.Set.SetAdminBannerInfo, formData, (): void => {});
        await drawerOnClose();
    }

    onUnmounted(() => rightDrawer.value = false);
    const drawerOnClose = async (): Promise<boolean> => rightDrawer.value = false;
</script>

<template>
    <div class="content-box banner-manner" v-loading="loading">
        <div class="button-sheet">
            <el-button type="primary" size="large" :icon="Plus" @click="createBanner">新增轮播</el-button>
            <el-button type="primary" size="large" :icon="Refresh" @click="refreshList">刷新列表</el-button>
        </div>

        <el-table :data="tableData" border>
            <el-table-column prop="bid" label="轮播编号" width="135" />
            <el-table-column prop="name" label="轮播标题" />
            <el-table-column prop="image" label="跳转链接" />
            <el-table-column prop="image" label="轮播图片" />

            <el-table-column label="更多操作" width="185">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="updateBanner(scope.row)">修改轮播</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteBanner(scope.row.bid)">删除轮播</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange" v-if="tableData.length > 0" />
    
        <FrameDrawer :title="`${formModel ? '创建' : '修改'}轮播`" :show="rightDrawer" @close="drawerOnClose">
            <el-form :model="formData" label-width="auto">
                <el-form-item label="轮播标题">
                    <el-input v-model="formData.name" placeholder="请输入轮播标题"/>
                </el-form-item>

                <el-form-item label="跳转链接">
                    <el-input v-model="formData.href" placeholder="请输入跳转链接"/>
                </el-form-item>

                <el-form-item label="轮播图片">
                    <el-input v-model="formData.image" placeholder="请输入轮播图片（链接）"/>
                </el-form-item>
            </el-form>
            <el-button type="primary" size="large" style="width: 100%; margin-top: 10px;" @click="sendCreateBanner">确认{{ formModel ? "创建" : "修改" }}</el-button>
        </FrameDrawer>
    </div>
</template>
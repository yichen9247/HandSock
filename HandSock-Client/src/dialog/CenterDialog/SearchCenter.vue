<script setup lang="ts">
    import Swal from 'sweetalert2'
    import HandUtils from '@/scripts/HandUtils'

    const searchInput = ref("");
    const searchOfChannel = () => {
        Swal.close()
        HandUtils.searchChannelByGid(searchInput.value);
    }
</script>

<template>
    <div class="search-channel">
        <el-input v-model="searchInput" placeholder="请输入要搜索的频道" clearable/>
        <el-button type="primary" @click="searchOfChannel">搜索频道</el-button>
    </div>
</template>

<style lang="less">
    div.search-channel {
        height: 40px;
        display: flex;

        div.el-input__wrapper {
            border-radius: 6px 0 0 6px;
        }

        button.el-button {
            height: 100%;
            border-radius: 0 6px 6px 0;
        }
    }
</style>
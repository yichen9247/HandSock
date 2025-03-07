<template>
    <RouterView/>
    <van-number-keyboard safe-area-inset-bottom />
</template>

<style lang="less">
    @import url("./assets/index.less");
    @import url("./assets/paces/pace.less");
    
    .swal2-image {
        border-radius: 4px;
    }

    div.swal2-modal {
        max-height: 95vh;
        overflow: hidden;
        overflow-y: scroll;
    }
</style>
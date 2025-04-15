<template>
    <RouterView/>
    <van-number-keyboard safe-area-inset-bottom />
</template>

<style lang="less">
    @import url("./assets/index.less");
    @import url("./assets/paces/pace.less");
    
    .swal2-image {
        border-radius: 6px;
    }

    div.swal2-modal {
        max-height: 95vh;
        overflow: hidden;
        overflow-y: scroll;
    }

    div.van-dialog {
        top: 50%!important;
        width: 260px!important;
    }

    div:where(.swal2-container) div:where(.swal2-popup) {
        border-radius: 6px!important;
    }
</style>
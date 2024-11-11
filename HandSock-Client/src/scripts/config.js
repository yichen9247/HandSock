export default {
    serverAdress: 'http://127.0.0.1:8081',
    serverIOAdress: '127.0.0.1:5120',

    serverApi: {
        uploadFile: "/api/upload/file",
        uploadAvatar: "/api/upload/avatar",
        uploadImages: "/api/upload/images",
        downloadFile: "/api/upload/download/file/",
        downloadAvatar: "/api/upload/download/avatar/",
        downloadImages: "/api/upload/download/images/",
    },

    dominColor: {
        defaultDominColor: 'rgba(5,160, 150, 1)',
        defaultDominColorV2: 'rgba(5,160, 150, 0.75)',

        refreshDominColor: 'rgba(75, 145, 225, 1)',
        refreshDominColorV2: 'rgba(75, 145, 225, 0.75)',

        pureshsDominColor: 'rgba(150, 180, 190, 1)',
        pureshsDominColorV2: 'rgba(150, 180, 190, 0.75)',

        yalanshDominColor: 'rgba(140, 200, 250, 1)',
        yalanshDominColorV2: 'rgba(140, 200, 250, 0.75)',
    }
};

/**
 * serverAdress: 后端地址 Type: String
 * serverIOAdress: 通信地址 Type: String
 * serverApi：后端配置，勿动此项 Type：Object
 * dominColor：勿动此项，此项为主题配置 Type：Object
 */
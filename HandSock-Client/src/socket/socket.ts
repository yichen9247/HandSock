export default {
    send: {
        ClientInit: '[CLIENT:INIT]',
        ReportUser: '[REPORT:USER]',
        OnlineLogin: '[ONLINE:LOGIN]',
        SendMessage: '[SEND:MESSAGE]',
        ClientCheck: '[CLIENT:CHECK]',
        SendAIChatMessage: '[SEND:AI:CHAT:MESSAGE]',

        Re: {
            ReforceLoad: '[RE:FORCE:LOAD]',
            ReforceConnect: '[RE:FORCE:CONNECT]',
            RehistoryClear: '[RE:HISTORY:CLEAR]'
        },

        Get: {
            GetSystemConfig: '[GET:SYSTEM:CONFIG]',
            GetSystemPlaylist: '[GET:SYSTEM:PLAYLIST]'
        },

        Set: {
            SetSystemTabooStatus: '[SET:SYSTEM:CONFIG:TABOO]',
            SetSystemUploadStatus: '[SET:SYSTEM:CONFIG:UPLOAD]'
        },

        User: {
            UserLogin: '[USER:LOGIN]',
            UserLoginout: '[USER:LOGOUT]',
            UserRegister: '[USER:REGISTER]',
            UserScanLogin: '[USER:SCAN:LOGIN]',
            UserScanLoginStatus: '[USER:SCAN:LOGIN:STATUS]'
        },

        Edit: {
            EditUserNick: '[EDIT:USER:NICK]',
            EditUserName: '[EDIT:USER:USERNAME]',
            EditUserAvatar: '[EDIT:USER:AVATAR]',
            EditUserPassword: '[EDIT:USER:PASSWORD]'
        },

        Search: {
            SearchGroup: '[SEARCH:GROUP]',
            SearchUserAll: '[SEARCH:USER:ALL]',
            SearchGroupAll: '[SEARCH:GROUP:ALL]',
            SearchHistoryAll: '[SEARCH:HISTORY:ALL]'
        },

        Admin: {
            Get: {
                GetAdminChanList: '[GET:ADMIN:CHAN:LIST]',
                GetAdminDashData: '[GET:ADMIN:DASH:DATA]',
                GetAdminChatList: '[GET:ADMIN:CHAT:LIST]',
                GetAdminUserList: '[GET:ADMIN:USER:LIST]',
                GetAdminRepoList: '[GET:ADMIN:REPO:LIST]',
                GetAdminBannerList: '[GET:ADMIN:BANNER:LIST]',
                GetAdminNoticeList: '[GET:ADMIN:NOTICE:LIST]',
                GetAdminSystemLogs: '[GET:ADMIN:SYSTEM:LOGS]',
                GetAdminUploadList: '[GET:ADMIN:UPLOAD:LIST]',
                GetAdminChatContent: '[GET:ADMIN:CHAT:CONTENT]',
                GetAdminSystemConfig: '[GET:SYSTEM:CONFIG]'
            },

            Add: {
                AddAdminChan: '[ADD:ADMIN:CHAN]',
                AddAdminBanner: '[ADD:ADMIN:BANNER]',
                AddAdminNotice: '[ADD:ADMIN:NOTICE]'
            },

            Set: {
                SetAdminChanInfo: '[SET:ADMIN:CHAN:INFO]',
                SetAdminBannerInfo: '[SET:ADMIN:BANNER:INFO]',
                SetAdminNoticeInfo: '[SET:ADMIN:NOTICE:INFO]',

                User: {
                    SetAdminUserInfo: '[SET:ADMIN:USER:INFO]',
                    SetAdminUserPassword: '[SET:ADMIN:USER:PASSWORD]',
                    SetAdminUserTabooStatus: '[SET:ADMIN:USER:TABOO:STATUS]'
                },

                System: {
                    SetSystemConfigTaboo: '[SET:SYSTEM:CONFIG:TABOO]',
                    SetSystemConfigValue: '[SET:SYSTEM:CONFIG:VALUE]',
                    SetSystenConfigUpload: '[SET:SYSTEM:CONFIG:UPLOAD]',
                    SetSystemConfigRegister: '[SET:SYSTEM:CONFIG:REGISTER]'
                }
            },

            Del: {
                DelAdminChan: '[DEL:ADMIN:CHAN]',
                DelAdminChat: '[DEL:ADMIN:CHAT]',
                DelAdminRepo: '[DEL:ADMIN:REPO]',
                DelAdminUser: '[DEL:ADMIN:USER]',
                DelAdminBanner: '[DEL:ADMIN:BANNER]',
                DelAdminNotice: '[DEL:ADMIN:NOTICE]',
                DelAdminUpload: '[DEL:ADMIN:UPLOAD]',
                DELAdminSystemLogs: '[DEL:ADMIN:SYSTEM:LOGS]'
            }
        }
    },
    rece: {
        Tokens: '[TOKENS]',
        Online: '[ONLINE]',
        Message: '[MESSAGE]',
        Warning: '[WARNING]',

        Re: {
            user: {
                ReuserAll: '[RE:USER:ALL]',
                ReuserNick: '[RE:USER:NICK]',
                ReuserName: '[RE:USER:USERNAME]',
                ReuserAvatar: '[RE:USER:AVATAR]'
            },
            force: {
                ReforceLoad: '[RE:FORCE:LOAD]',
                ReforceConnect: '[RE:FORCE:CONNECT]'
            },
            RehistoryClear: '[RE:HISTORY:CLEAR]'
        },

        AI: {
            CreateMessage: '[AI:CHAT:CREATE:MESSAGE]'
        }
    },

    devtool: {
        url: '/',
        clearLog: true,
        timeOutUrl: '/',
        disableCut: false,
        disableMenu: true,
        disableCopy: false,
        disablePaste: false,
        disableIframeParents: true,
    },

    server: {
        uploadFile: "/api/upload/file",
        uploadAvatar: "/api/upload/avatar", 
        uploadImages: "/api/upload/images",
        downloadFile: "/api/upload/download/file/",
        downloadAvatar: "/api/upload/download/avatar/",
        downloadImages: "/api/upload/download/images/",

        config: {
            baseUrl: import.meta.env.BASE_URL,
            serverIP: import.meta.env.VITE_SERVER_IP,
            serverUrl: import.meta.env.VITE_SERVER_URL,
            appDialog: import.meta.env.VITE_APP_DIALOG,
            autoShowImage: import.meta.env.VITE_AUTO_SHOW_IMAGE === 'true'
        }
    },

    dominColor: {
        // Default theme colors
        defaultDominColor: 'rgba(5,160, 150, 1)',

        // Refresh theme colors
        refreshDominColor: 'rgba(75, 145, 225, 1)',

        // Puresh theme colors  
        pureshsDominColor: 'rgba(150, 180, 190, 1)',

        // Yalansh theme colors
        yalanshDominColor: 'rgba(140, 200, 250, 1)',

        // Roufenh theme colors
        RoufenhDominColor: 'rgba(255, 119, 138, 1)',
    },

    application: {
        appName: 'HandSock',
        appVersion: '2.2.1-B2503221',
        author: 'https://github.com/yichen9247',
        authorName: 'yichen9247（Hua）',
        appDownload: 'https://doc.handsock.xiaokolomi.cn/apk/app-release.apk',
        github: 'https://github.com/yichen9247/HandSock',
        description: "HandSock 是一款有趣的聊天应用，基于 Typescript，Mybatis-Plus，Springboot, Vue3 和 Socket.io，Redis 等技术开发",

        updateLog: [{
            type: '优化',
            content: '优化了关于相关界面配色及布局'
        }, {
            type: '新增',
            content: '登录界面新增了扫码登录的方式'
        },  {
            type: '修复',
            content: '修复移动端注册时间异常的问题'
        }, {
            type: '优化',
            content: '优化登录逻辑多设备可同时在线'
        }, {
            type: '新增',
            content: '新增了项目容器化一键部署脚本'
        }]
    }
}
export default {
    send: {
        ClientInit: '[CLIENT:INIT]',
        ReportUser: '[REPORT:USER]',
        ClientPing: '[CLIENT:PING]',
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
            GetSystemConfig: '[GET:SYSTEM:CONFIG]'
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

        Forum: {
            AddForumPost: '[ADD:FORUM:POST]',
            AddCommentPost: '[ADD:FORUM:COMMENT]'
        },

        Search: {
            All: {
                SearchUserAll: '[SEARCH:USER:ALL]',
                SearchGroupAll: '[SEARCH:GROUP:ALL]',
                SearchNoticeAll: '[SEARCH:NOTICE:ALL]',
                SearchHistoryAll: '[SEARCH:HISTORY:ALL]'
            },
            SearchGroup: '[SEARCH:GROUP]',
            SearchForumPost: '[SEARCH:FORUM:POST]',
            SearchForumComment: '[SEARCH:FORUM:COMMENT]'
        },

        Admin: {
            Get: {
                GetAdminChanList: '[GET:ADMIN:CHAN:LIST]',
                GetAdminDashData: '[GET:ADMIN:DASH:DATA]',
                GetAdminChatList: '[GET:ADMIN:CHAT:LIST]',
                GetAdminUserList: '[GET:ADMIN:USER:LIST]',
                GetAdminRepoList: '[GET:ADMIN:REPO:LIST]',
                GetAdminPostList: '[GET:ADMIN:POST:LIST]',
                GetAdminBannerList: '[GET:ADMIN:BANNER:LIST]',
                GetAdminNoticeList: '[GET:ADMIN:NOTICE:LIST]',
                GetAdminSystemLogs: '[GET:ADMIN:SYSTEM:LOGS]',
                GetAdminUploadList: '[GET:ADMIN:UPLOAD:LIST]',
                GetAdminCommentList: '[GET:ADMIN:COMMENT:LIST]',
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
                    SetAdminUserStatus: '[SET:ADMIN:USER:STATUS]',
                    SetAdminUserPassword: '[SET:ADMIN:USER:PASSWORD]'
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
                DelAdminPost: '[DEL:ADMIN:POST]',
                DelAdminUser: '[DEL:ADMIN:USER]',
                DelAdminBanner: '[DEL:ADMIN:BANNER]',
                DelAdminNotice: '[DEL:ADMIN:NOTICE]',
                DelAdminUpload: '[DEL:ADMIN:UPLOAD]',
                DelAdminComment: '[DEL:ADMIN:COMMENT]',
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
            scanLogin: import.meta.env.VITE_APP_SCAN_LOGIN,
            autoDialog: import.meta.env.VITE_APP_AUTO_DIALOG,
            autoShowImage: import.meta.env.VITE_APP_AUTO_SHOWIMAGE === 'true'
        }
    },

    dominColor: {
        // Default theme colors
        defaultDominColor: 'rgba(75, 145, 225, 1)',

        // Puresh theme colors  
        pureshsDominColor: 'rgba(150, 180, 190, 1)',

        // Yalansh theme colors
        yalanshDominColor: 'rgba(140, 200, 250, 1)',

        // Roufenh theme colors
        roufenhDominColor: 'rgba(255, 119, 138, 1)',
    },

    application: {
        appName: 'HandSock',
        appVersion: '2.3.0-B15',
        author: 'https://github.com/yichen9247',
        authorName: 'yichen9247（Hua）',
        appDownload: 'https://doc.handsock.xiaokolomi.cn/apk/app-release.apk',
        github: 'https://github.com/yichen9247/HandSock',
        description: "HandSock是一款有趣且开源的聊天应用，集聊天室和社区为一体，基于 Springboot、Vue3、TypeScript 等技术开发",

        updateLog: [{
            type: '重构',
            content: '重构了消息收发模块'
        }, {
            type: '重构',
            content: '重构了弹窗调用模块'
        }, {
            type: '重构',
            content: '重构了权限认证模块'
        }, {
            type: '新增',
            content: '网页端新增社区板块'
        }, {
            type: '新增',
            content: '网页端新增公告板块'
        }, {
            type: '新增',
            content: '启用了管理项目折叠'
        }, {
            type: '新增',
            content: '新增了未读消息显示'
        }, {
            type: '新增',
            content: '新增了扫码登录模式'
        }, {
            type: '新增',
            content: '新增了一键部署脚本'
        }, {
            type: '新增',
            content: '新增推荐客户端弹窗'
        }, {
            type: '新增',
            content: '新增了后端开放接口'
        }, {
            type: '删除',
            content: '彻底删除了音乐模块'
        }, {
            type: '优化',
            content: '优化一系列用户体验'
        }, {
            type: '优化',
            content: '优化网页端性能速度'
        }, {
            type: '优化',
            content: '优化了网页端的样式'
        }, {
            type: '优化',
            content: '更换了默认主题颜色'
        }, {
            type: '修复',
            content: '修复了历史遗留问题'
        }, {
            type: '修复',
            content: '修复了偶现断连问题'
        }]
    }
}
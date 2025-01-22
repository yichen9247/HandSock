export default {
    send: {
        ClientInit: '[CLIENT:INIT]',
        ReportUser: '[REPORT:USER]',
        OnlineLogin: '[ONLINE:LOGIN]',
        SendMessage: '[SEND:MESSAGE]',
        ClientCheck: '[CLIENT:CHECK]',

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
            UserRegister: '[USER:REGISTER]'
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
                GetAdminSystemLogs: '[GET:ADMIN:SYSTEM:LOGS]',
                GetAdminUploadList: '[GET:ADMIN:UPLOAD:LIST]',
                GetAdminChatContent: '[GET:ADMIN:CHAT:CONTENT]',
                GetAdminSystemConfig: '[GET:SYSTEM:CONFIG]'
            },

            Add: {
                AddAdminChan: '[ADD:ADMIN:CHAN]'
            },

            Set: {
                SetAdminChanInfo: '[SET:ADMIN:CHAN:INFO]',

                User: {
                    SetAdminUserInfo: '[SET:ADMIN:USER:INFO]',
                    SetAdminUserPassword: '[SET:ADMIN:USER:PASSWORD]',
                    SetAdminUserTabooStatus: '[SET:ADMIN:USER:TABOO:STATUS]'
                },

                System: {
                    SetSystemConfigTaboo: '[SET:SYSTEM:CONFIG:TABOO]',
                    SetSystenConfigUpload: '[SET:SYSTEM:CONFIG:UPLOAD]',
                    SetSystemConfigPlayList: '[SET:SYSTEM:CONFIG:PLAYLIST]'
                }
            },

            Del: {
                DelAdminChan: '[DEL:ADMIN:CHAN]',
                DelAdminChat: '[DEL:ADMIN:CHAT]',
                DelAdminRepo: '[DEL:ADMIN:REPO]',
                DelAdminUser: '[DEL:ADMIN:USER]',
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
    },

    application: {
        appName: 'HandSock',
        appVersion: '2.0.1-B250101',
        author: 'https://github.com/yichen9247',
        authorName: 'yichen9247（Hua）',
        github: 'https://github.com/yichen9247/HandSock',
        description: "HandSock 是一款有趣的聊天应用，基于 Typescript，Mybatis-Plus，Springboot, Vue3 和 Socket.io，Redis 等技术开发",

        updateLog: [{
            type: '新增',
            content: '新增了举报系统，管理员支持禁言和删除内容等操作'
        }, {
            type: '新增',
            content: '管理后台新增查看系统日志和清空系统日志功能'
        },  {
            type: '新增',
            content: '设置界面新增背景音乐，后台可进行管理'
        }, {
            type: '修复',
            content: '修复了部分情况下在线用户常驻的问题'
        }, {
            type: '重构',
            content: '重构了大部分界面和配色布局风格'
        }, {
            type: '移除',
            content: '移除了桌面端设置弹窗内管理后台入口'
        }]
    }
}
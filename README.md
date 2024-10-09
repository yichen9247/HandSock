# [HandSock](https://github.com/handsock) &middot; [![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/yichen9247/HandSock/blob/main/LICENSE.txt) [![author](https://img.shields.io/badge/author-Hua-blue.svg)](http://suisuijiang.com) [![Node.js Version](https://img.shields.io/badge/node.js-16.20.2-blue.svg)](http://nodejs.org/download)

HandSock 是一款有趣的聊天应用，基于 Node.js, Vue3, Mysql 和 Socket.io 等技术开发

## 配置项目：服务端

创建一个空的数据库，然后修改 `server/database.js` 里面的的数据库连接信息

```js
module.exports = {
    port: 3306,
    user: 'root',
    host: 'localhost',
    password: '12345678',
    database: 'handsock',
    useConnectionPooling: true
}
```

配置 `server/config.js` 里面的各种信息，文件内容以及参数说明如下表所示：

```js
module.exports = {
    textValid: false,
    serverPort: 5100,
    checkToken: false,
    connectionFaild: false,
    clientCorsPassword: '12345678',
    alApiValidAsV2Token: 'alapi-token',
}

/**
 * textValid: 是否启用文本检验 Type: Boolean
 * serverPort：服务端（后端）端口 Type: String
 * checkToken: 是否启用客户端Token验证 Type: Boolean
 * connectionFaild: 如果不使用数据库存而是本地存储请设置 Type: String
 * clientCorsPassword：此项与客户端的clientCorsPassword一致 Type: String
 * alApiValidAsV2Token： AlAPI的Token，详见https://www.alapi.c/ Type: String
 */
```

## 配置项目：客户端

配置 `src/scripts/config.js` 里面的各种信息，文件内容以及参数说明如下表所示：

```js
export default {
    clientCorsPassword: '12345678',
    serverIOAdress: '127.0.0.1:5100',

    dominColor: {
        defaultDominColor: 'rgba(5,160, 150, 0.5)',
        refreshDominColor: 'rgba(75, 145, 225, 0.5)',
        pureshsDominColor: 'rgba(150, 180, 190, 0.5)',
        yalanshDominColor: 'rgba(140, 200, 250, 0.5)',
    }
};

/**
 * dominColor：勿动此项，此项为主题配置 Type：Object
 * serverIOAdress：服务端地址（格式为 IP:PORT）Type: String
 * clientCorsPassword: 此项与服务端的clientCorsPassword一致 Type: String
 */
```

配置 `src/scripts/channel.js` 里面的各种信息，文件内容以及参数说明如下表所示：

```js
export default [
    { id: 0, name: '猪的神奇聊天室', admin: 10036450571 },
    { id: 1, name: '猪的测试聊天室', admin: 10036450571 }
];

/**
 * 此为放开的频道列表，id为0的频道必须放开，否则将报错，name可以随便改
 * admin为用户的唯一标识ID，可访问前端查看
 * 格式：{ id: 0, name: '猪的神奇聊天室', admin: 10036450571 },
 */
```

## 启动项目：服务端

```bash
# 在此之前请先安装服务端的项目依赖
cd server && npm install # 在项目根目录执行

# 安装完依赖，运行服务端
npm run server # 在项目根目录执行
# 或者用另外一个命令
npm run server:io # 在项目根目录执行
```

## 启动项目：客户端

```bash
# 在此之前请先安装客户端的项目依赖
npm install # 在项目根目录执行

# 方式一：打包运行
npm run build # 在项目根目录执行
# 执行完后等待打包结束，打包结束后将dist静态文件目录放进服务器即可
# 或使用以下命令在原地运行打包之后的静态文件
npm run preview # 在项目根目录执行
# 接着，浏览器打开127.0.0.0:3000查看

# 方式二：以开发模式运行
npm run dev # 在项目根目录执行
# 接着，浏览器打开127.0.0.0:5173查看

```

## 清理记录：服务端

```sh
# 此命令将清理掉聊天记录，不用断开服务端也可执行
npm run clear # 在项目根目录执行
# 等待聊天记录清理结束...
```

此命令用于清理聊天记录，您的用户信息不会被清理掉，不管是MySQL还是JSON存储，只会清理掉聊天记录。

## 关于下个版本

下个版本可能会对整个HandSock消息系统和用户系统进行重构，且会放弃对本地JSON存储的支持，关于管理员功能以后可能会增加部分功能，欢迎提Issuses

## License

HandSock is [MIT licensed](./LICENSE.txt)
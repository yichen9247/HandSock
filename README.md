# [HandSock](https://github.com/yichen9247/HandSock) &middot; [![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/yichen9247/HandSock/blob/main/LICENSE.txt) [![author](https://img.shields.io/badge/author-Hua-blue.svg)](https://github.com/yichen9247) [![Node.js Version](https://img.shields.io/badge/node.js-16.20.2-blue.svg)](http://nodejs.org/download)

HandSock 是一款有趣的聊天应用，基于 Node.js, Vue3, Mysql，Redis 和 Socket.io 等技术开发

## 配置项目：服务端

配置 `server/redis.mjs` 里面的Redis连接信息：

```js
export default {
    port: 6379,
    host: '127.0.0.1',
    // password: '12345678'
}

/**
 * port: Redis端口号
 * host：Redis主机名（IP）
 * password: 如果设置了Redis密码，请配置此项
 */
```

创建一个空的数据库，然后修改 `server/database.mjs` 里面的的数据库连接信息

```js
export default {
    port: 3306,
    user: 'root',
    host: 'localhost',
    charset: 'utf8mb4',
    password: '12345678',
    database: 'handsock',
    useConnectionPooling: true,
    collation: 'utf8mb4_unicode_ci'
}

/**
 * collation: 关键，勿动此项
 * charset：数据库字符集，勿动此项
 */
```

配置 `server/config.mjs` 里面的各种信息，文件内容以及参数说明如下表所示：

```js
export default {
    textValid: false,
    serverPort: 5100,
    checkToken: false,
    retryConnection: true,
    connectionFaild: false,
    clientCorsPassword: '12345678',
    alApiValidAsV2Token: 'alapi-token',
}

/**
 * textValid: 是否启用文本检验 
 * serverPort：服务端（后端）端口
 * checkToken: 是否启用客户端Token验证
 * retryConnection: 尝试重新连接Mysql数据库
 * connectionFaild: 如果不想使用数据库存储而是本地存储，请设置为true
 * clientCorsPassword：此项与客户端的clientCorsPassword一致
 * alApiValidAsV2Token： AlAPI的Token，详见https://www.alapi.cn/
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

```bash
# 此命令将清理掉聊天记录，不用断开服务端也可执行
npm run clear # 在项目根目录执行
# 等待聊天记录清理结束...
```

此命令用于清理聊天记录，您的用户信息不会被清理掉，不管是MySQL还是JSON存储，只会清理掉聊天记录。

## 关于反向代理

如果你使用了反向代理，请修改Nginx或者Apache的配置文件，否则将错误识别客户端的IP

在Nginx的配置文件中，使用proxy_set_header指令来添加Forwarded头：

```js
server {
    listen 80;
    server_name example.com;

    location / {
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

在Apache的配置文件中，使用RequestHeader指令来添加Forwarded头：

```xml
<VirtualHost>
    ProxyPreserveHost On
</VirtualHost>
```

## 关于下个版本

下个版本可能会对整个HandSock消息系统和用户系统进行重构，且会放弃对本地JSON存储的支持，关于管理员功能以后可能会增加部分功能，欢迎提Issuses

## License

HandSock is [MIT licensed](https://github.com/yichen9247/HandSock/blob/main/LICENSE.txt)
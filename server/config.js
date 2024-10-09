/* eslint-disable no-undef */
module.exports = {
    textValid: false,
    serverPort: 5100,
    checkToken: false,
    connectionFaild: false,
    clientCorsPassword: '12345678',
    alApiValidAsV2Token: 'alapi-token',
}

/**
 * textValid: 是否启用文本检验 
 * serverPort：服务端（后端）端口
 * checkToken: 是否启用客户端Token验证
 * connectionFaild: 如果不想使用数据库存储而是本地存储，请设置为true
 * clientCorsPassword：此项与客户端的clientCorsPassword一致
 * alApiValidAsV2Token： AlAPI的Token，详见https://www.alapi.cn/
 */
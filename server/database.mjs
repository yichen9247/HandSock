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
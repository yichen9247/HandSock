package com.server.handsock.checks;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlChecker {

    private final String USER_TABLE_NAME = "handsock_user";
    private final String BANNER_TABLE_NAME = "handsock_banner";
    private final String NOTICE_TABLE_NAME = "handsock_notice";
    private final String REPORT_TABLE_NAME = "handsock_report";
    private final String SYSTEM_TABLE_NAME = "handsock_system";
    private final String UPLOAD_TABLE_NAME = "handsock_upload";
    private final String MESSAGE_TABLE_NAME = "handsock_message";
    private final String CHANNEL_TABLE_NAME = "handsock_channel";

    private final String CREATE_USER_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `uid` bigint(20) NOT NULL,
                    `nick` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '热心网友',
                    `taboo` varchar(5) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'close',
                    `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                    `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'https://dn-qiniu-avatar.qbox.me/avatar/',
                    `ai_auth` int(1) NOT NULL DEFAULT '0',
                    `reg_time` datetime DEFAULT CURRENT_TIMESTAMP,
                    `is_robot` int(1) NOT NULL DEFAULT '0',
                    `is_admin` int(1) NOT NULL DEFAULT '0',
                    `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                    `password` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                    PRIMARY KEY (uid)
                ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(USER_TABLE_NAME);

    private final String CREATE_SYSTEM_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `yid` bigint(5) NOT NULL,
                    `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                    `value` text COLLATE utf8mb4_unicode_ci,
                    PRIMARY KEY (yid)
                ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(SYSTEM_TABLE_NAME);

    private final String CREATE_UPLOAD_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `fid` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `uid` bigint(20) NOT NULL,
                    `size` bigint(20) NOT NULL,
                    `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    `name` text COLLATE utf8mb4_unicode_ci NOT NULL,
                    `path` text COLLATE utf8mb4_unicode_ci NOT NULL,
                    `type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
                    PRIMARY KEY (fid)
                ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(UPLOAD_TABLE_NAME);

    private final String CREATE_REPORT_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `rid` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `sid` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    `reason` text COLLATE utf8mb4_unicode_ci NOT NULL,
                    `deleted` int(1) DEFAULT '0',
                    `reporter_id` bigint(20) NOT NULL,
                    `reported_id` bigint(20) NOT NULL,
                    PRIMARY KEY (rid)
                ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(REPORT_TABLE_NAME);

    private final String CREATE_MESSAGE_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `sid` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `uid` bigint(20) NOT NULL,
                    `gid` bigint(20) NOT NULL,
                    `type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    `address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                    `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
                    `deleted` int(1) DEFAULT '0',
                    PRIMARY KEY (sid)
                ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(MESSAGE_TABLE_NAME);

    private final String CREATE_CHANNEL_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `gid` bigint(20) NOT NULL,
                    `name` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `open` int(1) NOT NULL DEFAULT '1',
                    `home` int(1) NOT NULL DEFAULT '0',
                    `active` int(1) NOT NULL DEFAULT '0',
                    `avatar` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                    `notice` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '当前频道暂无公告',
                    `ai_role` int(1) NOT NULL DEFAULT '0',
                    PRIMARY KEY (gid)
                ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(CHANNEL_TABLE_NAME);

    private final String CREATE_BANNER_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `bid` int(11) NOT NULL AUTO_INCREMENT,
                    `name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `href` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
                    `image` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
                    PRIMARY KEY (bid)
                ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(BANNER_TABLE_NAME);

    private final String CREATE_NOTICE_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `nid` int(11) NOT NULL AUTO_INCREMENT,
                    `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    `title` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `content` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
                    PRIMARY KEY (nid)
                ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(NOTICE_TABLE_NAME);

    private final String INSERT_USER_SQL = """
                INSERT INTO %s (`uid`, `nick`, `taboo`, `email`, `avatar`, `ai_auth`, `reg_time`, `is_robot`, `is_admin`, `username`, `password`) VALUES
                    (2400442120, 'AdminUser', 'close', NULL, '0/default/0.png', 1, CURRENT_TIMESTAMP, 0, 1, 'handsock', 'd0775d11a4c67b9e8df44c336fac3371'),
                    (2400408600, 'RobotUser', 'close', NULL, '0/default/1.png', 0, CURRENT_TIMESTAMP, 1, 0, 'robot123', 'c51ce410c124a10e0db5e4b97fc2af39');
            """.formatted(USER_TABLE_NAME);

    private final String INSERT_SYSTEM_SQL = """
                INSERT INTO %s (`yid`, `name`, `value`) VALUES
                    (1, 'taboo', 'close'),
                    (2, 'upload', 'open'),
                    (3, 'register', 'open'),
                    (4, 'playlist', '2520739691'),
                    (5, 'version', '2.1.2'),
                    (6, 'download', 'http://lc-6piuorK4.cn-n1.lcfile.com/lPKEysQMNIkkHcSSgl7mHrHq7HuDBrPE/app-release.apk');
            """.formatted(SYSTEM_TABLE_NAME);

    private final String INSERT_CHANNEL_SQL = """
                INSERT INTO %s (`gid`, `name`, `open`, `home`, `active`, `avatar`, `ai_role`) VALUES
                    (0, '猪的神奇聊天室', 1, 1, 1, '/image/avatar.jpeg', 0),
                    (1, '猪的测试聊天室', 1, 0, 1, '/image/avatar.jpeg', 0),
                    (2, '人工智能聊天室', 1, 0, 1, '/image/chatgpt.png', 1),
                    (3, '项目进展阅览室', 1, 0, 1, '/image/handsock.png', 0);
            """.formatted(CHANNEL_TABLE_NAME);

    private final String INSERT_BANNER_SQL = """
                INSERT INTO %s (`bid`, `name`, `href`, `image`) VALUES
                    (1, 'APP正式上线啦', 'https://doc.handsock.xiaokolomi.cn/depoly/android.html', 'https://mmbiz.qpic.cn/sz_mmbiz_png/67YpKzccxKeXCwkeSl8nRuLxKgiazZpB5Ll4M1eH41xuqfXPkJ4LUAmU3ExWxOJJpoiaFSW51LYN2TD25VVncw4Q/640?wx_fmt=png&amp;from=appmsg'),
                    (2, '加入我们的群聊', 'https://qm.qq.com/q/AKdIXRHMBM', 'https://mmbiz.qpic.cn/sz_mmbiz_png/67YpKzccxKeXCwkeSl8nRuLxKgiazZpB5fUSmEWYhBoLYAKMibHNuZgiaJ38aNAAAdd7MsNe9nk6eFRpB3erDSG1g/640?wx_fmt=png&amp;from=appmsg'),
                    (3, '聊天类软件定制', 'hhttps://doc.handsock.xiaokolomi.cn/depoly/android.html', 'https://mmbiz.qpic.cn/sz_mmbiz_png/67YpKzccxKeXCwkeSl8nRuLxKgiazZpB5EfMor7ibBvicEyhALLNv2b6XIbbT0BBRzamcd62dM9pB8p0u4KhWeTibA/640?wx_fmt=png&amp;from=appmsg'),
                    (4, '广告位限时招租', 'https://doc.handsock.xiaokolomi.cn/depoly/ad-leasing.html', 'https://mmbiz.qpic.cn/sz_mmbiz_png/67YpKzccxKeXCwkeSl8nRuLxKgiazZpB5thy1VFlvqxoLxeBKOoqyYdLzngibwEib0KvgnzsBgwGmcZXLibI6gRLkw/640?wx_fmt=png&amp;from=appmsg');
            """.formatted(BANNER_TABLE_NAME);

    public void checkTable(String databaseDriver, String databaseUrl, String databaseUsername, String databasePassword) throws ClassNotFoundException, SQLException {
        Class.forName(databaseDriver);
        Connection connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
        DatabaseMetaData dbMetaData = connection.getMetaData();
        if (!dbMetaData.getTables(null, null, USER_TABLE_NAME, null).next()) createUserTable(connection);
        if (!dbMetaData.getTables(null, null, BANNER_TABLE_NAME, null).next()) createBannerTable(connection);
        if (!dbMetaData.getTables(null, null, NOTICE_TABLE_NAME, null).next()) createNoticeTable(connection);
        if (!dbMetaData.getTables(null, null, SYSTEM_TABLE_NAME, null).next()) createSystemTable(connection);
        if (!dbMetaData.getTables(null, null, UPLOAD_TABLE_NAME, null).next()) createUploadTable(connection);
        if (!dbMetaData.getTables(null, null, REPORT_TABLE_NAME, null).next()) createReportTable(connection);
        if (!dbMetaData.getTables(null, null, MESSAGE_TABLE_NAME, null).next()) createMessageTable(connection);
        if (!dbMetaData.getTables(null, null, CHANNEL_TABLE_NAME, null).next()) createChannelTable(connection);
    }

    public void createUserTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_USER_TABLE_SQL);
        connection.createStatement().execute(INSERT_USER_SQL);
    }

    public void createBannerTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_BANNER_TABLE_SQL);
        connection.createStatement().execute(INSERT_BANNER_SQL);
    }

    public void createNoticeTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_NOTICE_TABLE_SQL);
    }

    public void createSystemTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_SYSTEM_TABLE_SQL);
        connection.createStatement().execute(INSERT_SYSTEM_SQL);
    }

    public void createUploadTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_UPLOAD_TABLE_SQL);
    }

    public void createReportTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_REPORT_TABLE_SQL);
    }

    public void createMessageTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_MESSAGE_TABLE_SQL);
    }

    public void createChannelTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_CHANNEL_TABLE_SQL);
        connection.createStatement().execute(INSERT_CHANNEL_SQL);
    }
}
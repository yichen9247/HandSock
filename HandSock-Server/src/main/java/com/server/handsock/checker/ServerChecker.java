package com.server.handsock.checker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.server.handsock.console.ConsolePrints;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.connection.RedisConnectionCommands;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ServerChecker {

    private final JdbcTemplate jdbcTemplate;
    private final StringRedisTemplate redisTemplate;
    private final MysqlChecker mysqlChecker = new MysqlChecker();

    @Autowired
    public ServerChecker(JdbcTemplate jdbcTemplate, StringRedisTemplate redisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisTemplate = redisTemplate;
    }

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ConsolePrints consolePrints = new ConsolePrints();

    private static void shutdown(String content) {
        consolePrints.printErrorLog(content);
        System.exit(0);
    }

    public void checkMysqlConnection() {
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT 1");
            if (!result.isEmpty()) {
                consolePrints.printSuccessLog("Mysql server connect success!");
            } else shutdown("Redis server connect failed!");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            shutdown("Redis server connect failed!");
        }
    }

    public void checkRedisConnection() {
        try {
            String pingResult = redisTemplate.execute(RedisConnectionCommands::ping);
            if ("PONG".equalsIgnoreCase(pingResult)) {
                consolePrints.printSuccessLog("Redis server connect success!");
            } else shutdown("Mysql server connect failed!");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            shutdown("Mysql server connect failed!");
        }
    }

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Value("${spring.datasource.driver-class-name}")
    private String databaseDriver;

    public void checkDatabaseTable() {
        try {
            consolePrints.printInfoLogV2("Please ignore the error of BadSqlGrammarException, because we will retrieve and fix it.");
            mysqlChecker.checkTable(databaseDriver, databaseUrl, databaseUsername, databasePassword);
            consolePrints.printSuccessLog("Database table check success!");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            shutdown("Database table checked failed!");
        }
    }
}
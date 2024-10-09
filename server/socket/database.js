/* eslint-disable no-undef */

const fs = require('fs');
const { styleText } = require('node:util');
const { writeRunningLog } = require('../scripts/utils')

let synconce = 0;
const connectServer = async (connection, connectionFaild, syncLocalHistory) => {
    connection.connect((error) => {  
        if (error) {
            console.log(styleText('red', '[CHECK]：Failed to connect to MySQL database!'));
            return process.exit(1);
        }
        syncLocalHistory.databaseSYncWithLocation();
        console.log(styleText('green', '[SERVER]：Server：Connected to the MySQL server!'));

        setInterval(() => {
            connection.query('SELECT 1', (error) => {
              if (error) return console.log(styleText('red', '[SERVER]：Error while performing Query!'));
            });
        }, 1000);
    });
}

const registerUser = (connection, connectionFaild, userid, username, userqq) => {
    !connectionFaild && connection.query('SHOW TABLES LIKE "users"', (error) => {
        if (error) throw error;
        connection.query('SELECT * FROM users WHERE username = ?', [username], (error) => {
            if (error) throw error;
            const query = 'INSERT INTO users (userid, username, userqq) VALUES (?, ?, ?)';
            connection.query(query, [userid, username, userqq], (error) => {
                if (error) throw error;
            });
        });
    });
}

const clearHistory = (connection, content, broadcastMessage) => {
    connection.query('SELECT chatData FROM history', (error, results) => {
        if (error) throw error;
        const query = 'UPDATE history SET chatData = ?';
        let historyText = JSON.stringify(["{\"code\":0,\"name\":\"system\",\"message\":\"null\",\"userid\":\"0\",\"userqq\":0,\"channel\":\"0\",\"time\":\"2023-12-24 13:27\",\"chatCode\":\"0\"}"]);
        connection.query(query, historyText, (error) => {
            if (error) throw error;
            let dataJson = JSON.parse(results[0].chatData);
            dataJson.push(content);
            connection.query(query, historyText, (error) => {
                if (error) throw error;
                broadcastMessage(content);
            });
        });
    });

    fs.writeFile('cache/history.json', JSON.stringify(["{\"code\":0,\"name\":\"system\",\"message\":\"null\",\"userid\":\"0\",\"userqq\":0,\"channel\":\"0\",\"time\":\"2023-12-24 13:27\",\"chatCode\":\"0\"}"]), () => {
        writeRunningLog("History clear complete!\n");
        fs.readFile('cache/history.json', 'utf8', (error, data) => {
            let dataJson = JSON.parse(data);
            dataJson.push(content);
            fs.writeFile('cache/history.json', JSON.stringify(dataJson), () => broadcastMessage(content));
        });
    });
};


const databasePushHistory = (connection, connectionFaild, content, broadcastMessage) => {
    if (error) throw error;
    connectionFaild && fs.readFile('cache/history.json', 'utf8', (error, data) => {
        let dataJson = JSON.parse(data);
        if (dataJson.length > 600) {
            broadcastMessage({ code: 500, message: "服务端聊天记录已重置！" });
            clearHistory(connection, content, broadcastMessage);
        } else {
            dataJson.push(content);
            fs.writeFile('cache/history.json', JSON.stringify(dataJson), () => broadcastMessage(content));
        }
    });

    !connectionFaild && connection.query('SELECT chatData FROM history', (error, results) => {
        if (error) throw error;
        let dataJson = JSON.parse(results[0].chatData);
        if (dataJson.length > 600) {
            broadcastMessage({ code: 500 });
            clearHistory(connection, content, broadcastMessage);
        } else {
            dataJson.push(JSON.stringify(content));
            databaseClearWithLocation();
            const query = 'UPDATE history SET chatData = ?';
            connection.query(query, JSON.stringify(dataJson), (error) => {
                if (error) throw error;
                broadcastMessage(content);
            });
        }
    });
}

const databaseClearWithLocation = (syncLocalHistory) => {
    if (!(synconce <= 20)) {
        synconce = 0;
        syncLocalHistory.databaseSYncWithLocation();
    } else synconce++;
}

module.exports = { registerUser, clearHistory, connectServer, databaseClearWithLocation, databasePushHistory }

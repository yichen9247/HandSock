/* eslint-disable no-undef */

import fs from 'fs';
import { styleText } from 'util';
import message from './message.mjs';
import syncLocalHistory from '../class/sync.mjs';
import { writeRunningLog } from '../scripts/utils.mjs';

let synconce = 0;
export const connectServer = async (connection) => {
    connection.connect((error) => {  
        if (error) {
            console.log(styleText('red', '[CHECK]：Failed to connect to MySQL Server!'));
            return process.exit(1);
        }
        syncLocalHistory.databaseSYncWithLocation();
        console.log(styleText('green', '[SERVER]：Server：Connected to the MySQL Server!'));
    });
}

export const clearHistory = (connection, content, broadcastMessage) => {
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


export const databasePushHistory = (connection, connectionFaild, content, broadcastMessage, address) => {
    let logContent = content;
    logContent.address = address;
    writeRunningLog("\n" + message.messageType[content.code] + JSON.stringify(logContent));

    connectionFaild && fs.readFile('cache/history.json', 'utf8', (error, data) => {
        if (error) throw error;
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

export const databaseClearWithLocation = () => {
    if (!(synconce <= 20)) {
        synconce = 0;
        syncLocalHistory.databaseSYncWithLocation();
    } else synconce++;
}

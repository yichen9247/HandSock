/* eslint-disable no-undef */
import fs from 'fs';
import path from 'path';
import mysql from 'mysql';
import express from 'express';
import config from '../config.mjs';
import message from './message.mjs';
import database from '../database.mjs';
import robot from '../robot/index.mjs';
import command from '../robot/command.mjs';

import { styleText } from 'util';
import { Server } from 'socket.io';
import { createServer } from 'http';
import { fileURLToPath } from 'url';
import { setRedisIPCatch } from './redisClient.mjs';
import { databasePushHistory, connectServer } from './database.mjs';
import { returnThisTime, checkChatTextValid, returnServerToken, extractIPv4FromIPv6 } from '../scripts/utils.mjs';

process.on('uncaughtException',async (error) => {
    if (error.code === 'PROTOCOL_CONNECTION_LOST') {
        console.log(styleText('red', '[SERVER]：The Mysql server close the connecion!'));
        console.log(styleText('yellow', '[SERVER]：Retry connecting to the Mysql Server will!'));
        await connectToMysql();
    }
});

let connectionFaild = config.connectionFaild;
let onlineUsers = 0, connection = null, accessList = [];

const application = express();
const server = createServer(application);
const ioServer = new Server(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
    },
    pingTimeout: 60000
});
const __filename = fileURLToPath(import.meta.url);

const __dirname = path.dirname(__filename);
config.checkToken && ioServer.use((socket, next) => {
    let address = socket.handshake.address;
    let headers = socket.handshake.headers['user-agent'];
    let ipAddress = socket.handshake.headers["x-forwarded-for"];
    if (ipAddress !== undefined) address = ipAddress.split(",")[0];
    if (accessList.some(item => item.address === address)) {
        let client = accessList.find(item => item.address === address);
        if (client.auth.some(item => item.headers === headers)) {
            if (socket.handshake.auth.token === client.auth.find(item => item.headers === headers).token) next();
        } else next();
    } else next();
});

const connectToMysql = async () => {
    connection = mysql.createConnection(database);
    connectServer(connection, connectionFaild);
}

if (!config.connectionFaild) {
    await connectToMysql();
} else console.log('Server：Connected to the LocalStorage server!\n');

const initChatServer = (socket) => {
    connectionFaild && fs.readFile('cache/history.json', 'utf8', (error, data) => {
        let dataJson = data;
        socket.emit(`[${message.boardType[102]}：${102}]`, { code: 102, data: JSON.parse(dataJson) });
    });

    !connectionFaild && connection.query('SELECT chatData FROM history', (error, results) => {
        if (error) throw error;
        let dataJson = results[0].chatData;
        socket.emit(`[${message.boardType[102]}：${102}]`, { code: 102, data: JSON.parse(dataJson) });
    });
}
const broadcastMessage = (content) => ioServer.emit(`[${message.boardType[content.code]}：${content.code}]`, content);

ioServer.on('connection', (socket) => {
    onlineUsers++;
    initChatServer(socket);
    let address = socket.handshake.address;
    let headers = socket.handshake.headers['user-agent'];
    let ipAddress = socket.handshake.headers["x-forwarded-for"];
    if (ipAddress !== undefined) address = ipAddress.split(",")[0];

    if (!accessList.some(item => item.address === address)) {
        accessList.push({
            auth: [{
                token: returnServerToken(),
                headers: headers,
            }],
            address: address
        });
        console.log(styleText('blue', `[CLIENT：${extractIPv4FromIPv6(address)}]：Has new IP address visit!`));
    } else {
        let client = accessList.find(item => item.address === address);
        if (!client.auth.some(item => item.headers === headers)) {
            client.auth.push({
                token: returnServerToken(),
                headers: headers,
            });
            console.log(styleText('blue', `[CLIENT：${extractIPv4FromIPv6(address)}]：Has new same IP address but headers visit!`));
        }
    }
    
    let client = accessList.find(item => item.address === address);
    ioServer.emit(`[${message.boardType[100]}：${100}]`, { code: 100, data: onlineUsers });
    socket.emit('[TOKEN]', { code: 104, token: client.auth.find(item => item.headers === headers).token });

    socket.on('disconnect', () => {
        onlineUsers--;
        ioServer.emit(`[${message.boardType[100]}：${100}]`, { code: 100, data: onlineUsers });
    });

    socket.on('[JOIN：101]', async (messageObject) => {
        if (await setRedisIPCatch(address)) return socket.emit('[WARNING]', { code: 400, message: "发送消息过快" });
        if (messageObject.clientCorsPassword !== config.clientCorsPassword) return socket.emit('[WARNING]', { code: 400, message: "连接被拒绝" });
        databasePushHistory(connection, connectionFaild, { 'code': 101, name: '系统', message: messageObject.name + '加入到了聊天室', userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime() }, broadcastMessage, extractIPv4FromIPv6(address));
    });

    socket.on('[CHAT：103]', async (messageObject) => {
        if (await setRedisIPCatch(address)) return socket.emit('[WARNING]', { code: 400, message: "发送消息过快" });
        if (messageObject.clientCorsPassword !== config.clientCorsPassword) return socket.emit('[WARNING]', { code: 400, message: "连接被拒绝" });
        databasePushHistory(connection, connectionFaild, { 'code': 103, name: messageObject.name, message: messageObject.message, userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime(), chatCode: messageObject.chatCode }, broadcastMessage, extractIPv4FromIPv6(address));
    });

    socket.on('[CHAT：201]', async (messageObject) => {
        if (await setRedisIPCatch(address)) return socket.emit('[WARNING]', { code: 400, message: "发送消息过快" });
        if (messageObject.clientCorsPassword !== config.clientCorsPassword) return socket.emit('[WARNING]', { code: 400, message: "连接被拒绝" });
        if (command.list.some(item => item.keyword === messageObject.message)) {
            databasePushHistory(connection, connectionFaild, { 'code': 201, name: messageObject.name, message: messageObject.message, userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime(), chatCode: messageObject.chatCode }, broadcastMessage, extractIPv4FromIPv6(address));
            robot.robotChat(messageObject.message).then((value) => {
                databasePushHistory(connection, connectionFaild, { code: 203, name: command.name, message: value, userid: 66600000, channel: messageObject.channel, userqq: "1708910253", time: returnThisTime(), chatCode: 0 }, broadcastMessage, extractIPv4FromIPv6(address));
            });
        } else 
        config.textValid ? await checkChatTextValid(messageObject.message).then(async (data) => {
            if (data.code === 200 && data.data.conclusion === '不合规') return socket.emit('[WARNING]', { code: 401, message: "请注意措辞" });
            databasePushHistory(connection, connectionFaild, { 'code': 201, name: messageObject.name, message: messageObject.message, userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime(), chatCode: messageObject.chatCode }, broadcastMessage, extractIPv4FromIPv6(address));
        }) : databasePushHistory(connection, connectionFaild, { 'code': 201, name: messageObject.name, message: messageObject.message, userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime(), chatCode: messageObject.chatCode }, broadcastMessage, extractIPv4FromIPv6(address));
    });
});

application.get('/', (req, res) => res.sendFile(path.join(__dirname, '../statics/index.html')));
server.listen(config.serverPort, () => console.log(styleText('green', `[SERVER]：Server Running at http://localhost:${config.serverPort}`)));
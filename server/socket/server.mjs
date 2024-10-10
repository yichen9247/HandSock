/* eslint-disable no-undef */
import fs from 'fs';
import path from 'path';
import mysql from 'mysql';
import express from 'express';
import config from '../config.mjs';
import database from '../database.mjs';
import robot from '../robot/index.mjs';
import command from '../robot/command.mjs';

import { styleText } from 'util';
import { Server } from 'socket.io';
import { createServer } from 'http';
import { fileURLToPath } from 'url';
import { databasePushHistory, connectServer } from './database.mjs';
import { returnThisTime, writeRunningLog, checkChatTextValid, returnServerToken, extractIPv4FromIPv6 } from '../scripts/utils.mjs';

const application = express();
const server = createServer(application);
const ioServer = new Server(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
    },
});
const __filename = fileURLToPath(import.meta.url);

let assecc = [];
const __dirname = path.dirname(__filename);
config.checkToken && ioServer.use((socket, next) => {
    let address = socket.handshake.address;
    let headers = socket.handshake.headers['user-agent'];
    let ipAddress = socket.handshake.headers["x-forwarded-for"].split(",")[0];
    if (ipAddress !== undefined) address = ipAddress;
    if (assecc.some(item => item.address === address)) {
        let client = assecc.find(item => item.address === address);
        if (client.auth.some(item => item.headers === headers)) {
            if (socket.handshake.auth.token === client.auth.find(item => item.headers === headers).token) next();
        } else next();
    } else next();
});

let onlineUsers = 0;
let connection = null;
let connectionFaild = config.connectionFaild;

if (!config.connectionFaild) {
    connection = mysql.createConnection(database);
    connectServer(connection, connectionFaild);
} else console.log('Server：Connected to the LocalStorage server!\n');


const initChatServer = (socket) => {
    connectionFaild && fs.readFile('cache/history.json', 'utf8', (error, data) => {
        let dataJson = data;
        socket.emit(`[${boardType[102]}：${102}]`, { code: 102, data: JSON.parse(dataJson) });
    });

    !connectionFaild && connection.query('SELECT chatData FROM history', (error, results) => {
        if (error) throw error;
        let dataJson = results[0].chatData;
        socket.emit(`[${boardType[102]}：${102}]`, { code: 102, data: JSON.parse(dataJson) });
    });
}
const broadcastMessage = (message) => ioServer.emit(`[${boardType[message.code]}：${message.code}]`, message);

ioServer.on('connection', (socket) => {
    onlineUsers++;
    initChatServer(socket);
    let address = socket.handshake.address;
    let headers = socket.handshake.headers['user-agent'];
    let ipAddress = socket.handshake.headers["x-forwarded-for"].split(",")[0];

    if (ipAddress !== undefined) address = ipAddress;

    if (!assecc.some(item => item.address === address)) {
        assecc.push({
            auth: [{
                token: returnServerToken(),
                headers: headers,
            }],
            address: address
        });
        console.log(styleText('blue', `[CLIENT：${extractIPv4FromIPv6(address)}]：Has new IP address visit!`));
    } else {
        let client = assecc.find(item => item.address === address);
        if (!client.auth.some(item => item.headers === headers)) {
            client.auth.push({
                token: returnServerToken(),
                headers: headers,
            });
            console.log(styleText('blue', `[CLIENT：${extractIPv4FromIPv6(address)}]：Has new same IP address but headers visit!`));
        }
    }
    
    let client = assecc.find(item => item.address === address);
    ioServer.emit(`[${boardType[100]}：${100}]`, { code: 100, data: onlineUsers });
    socket.emit('[TOKEN]', { code: 104, token: client.auth.find(item => item.headers === headers).token });

    socket.on('disconnect', () => {
        onlineUsers--;
        ioServer.emit(`[${boardType[100]}：${100}]`, { code: 100, data: onlineUsers });
    });

    socket.on('[JOIN：101]', async (messageObject) => {
        if (messageObject.clientCorsPassword !== config.clientCorsPassword) return socket.emit('[WARNING]', { code: 403, message: "连接被拒绝" });
        databasePushHistory(connection, connectionFaild, { 'code': 101, name: '系统', message: messageObject.name + '加入到了聊天室', userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime() }, broadcastMessage);
        writeRunningLog(messageType[101] + JSON.stringify({ 'code': 101, name: '系统', message: messageObject.name + '加入到了聊天室', userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime(), address: extractIPv4FromIPv6(address) }) + "\n");
    });

    socket.on('[CHAT：103]', async (messageObject) => {
        if (messageObject.clientCorsPassword !== config.clientCorsPassword) return socket.emit('[WARNING]', { code: 403, message: "连接被拒绝" });
        databasePushHistory(connection, connectionFaild, { 'code': 103, name: messageObject.name, message: messageObject.message, userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime(), chatCode: messageObject.chatCode }, broadcastMessage);
        writeRunningLog(messageType[103] + JSON.stringify({ 'code': 103, name: messageObject.name, message: messageObject.message, userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime(), chatCode: messageObject.chatCode, address: extractIPv4FromIPv6(address) }) + "\n");
    });

    socket.on('[CHAT：201]', async (messageObject) => {
        if (messageObject.clientCorsPassword !== config.clientCorsPassword) return socket.emit('[WARNING]', { code: 403, message: "连接被拒绝" });
        if (command.list.some(item => item.keyword === messageObject.message)) {
            databasePushHistory(connection, connectionFaild, { 'code': 201, name: messageObject.name, message: messageObject.message, userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime(), chatCode: messageObject.chatCode }, broadcastMessage);
            writeRunningLog(messageType[103] + JSON.stringify({ 'code': 103, name: messageObject.name, message: messageObject.message, userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime(), chatCode: messageObject.chatCode, address: extractIPv4FromIPv6(address) }) + "\n");
            
            robot.robotChat(messageObject.message).then((value) => {
                databasePushHistory(connection, connectionFaild, { code: 203, name: command.name, message: value, userid: 66600000, channel: messageObject.channel, userqq: "1708910253", time: returnThisTime(), chatCode: 0 }, broadcastMessage);
                writeRunningLog(messageType[203] + JSON.stringify({ 'code': 203, name: command.name, message: value, userid: 66600000, channel: messageObject.channel, userqq: "1708910253", time: returnThisTime(), chatCode: 0, address: extractIPv4FromIPv6(address) }) + "\n");
            });
        } else 
        if (config.textValid) {
            await checkChatTextValid(messageObject.message).then(async (data) => {
                if (data.code === 200 && data.data.conclusion === '不合规') {
                    socket.emit('[WARNING]', { code: 400, message: "请注意措辞" });
                } else {
                    databasePushHistory(connection, connectionFaild, { 'code': 201, name: messageObject.name, message: messageObject.message, userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime(), chatCode: messageObject.chatCode }, broadcastMessage);
                    writeRunningLog(messageType[201] + JSON.stringify({ 'code': 201, name: messageObject.name, message: messageObject.message, userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime(), chatCode: messageObject.chatCode, address: extractIPv4FromIPv6(address) }) + "\n");
                }
            });
        } else {
            databasePushHistory(connection, connectionFaild, { 'code': 201, name: messageObject.name, message: messageObject.message, userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime(), chatCode: messageObject.chatCode }, broadcastMessage);
            writeRunningLog(messageType[201] + JSON.stringify({ 'code': 201, name: messageObject.name, message: messageObject.message, userid: messageObject.userid, channel: messageObject.channel, userqq: messageObject.userqq, time: returnThisTime(), chatCode: messageObject.chatCode, address: extractIPv4FromIPv6(address) }) + "\n");
        }
    });
});

application.get('/', (req, res) => res.sendFile(path.join(__dirname, '../statics/index.html')));
server.listen(config.serverPort, () => console.log(styleText('green', `[SERVER]：Server Running at http://localhost:${config.serverPort}`)));
const boardType = { 100: 'ONLINE', 101: 'JOIN', 102: 'HISTORY', 103: 'CLAP', 104: 'TOKEN', 201: 'CHAT', 203: 'ROBOT', 400: 'Valid', 403: 'CROS', 500: 'SERVER' };
const messageType = { 100: '[ONLINE]', 101: '[JOIN]', 102: '[HISTORY]', 103: '[CLAP]', 104: '[TOKEN]', 201: '[CHAT]', 203: '[ROBOT]', 400: '[Valid]', 403: '[CROS]', 500: '[SERVER]' };

/* eslint-disable no-undef */

const fs = require('fs');
const axios = require('axios');
const config = require('../config.js');
const { v4: uuidv4 } = require('uuid');
const { styleText } = require('node:util');
const dateTool = require('../class/time.js');

const returnThisTime = () => {
    const date = new dateTool().Date();
    return date.Year + "-" + date.Month + "-" + date.Date + " " + date.Hou + ":" + date.Min;
}

const writeRunningLog = (content) => {
    console.log(styleText('grey', content));
    fs.appendFile('cache/running.log', content, () => { });
}

const returnServerToken = () => {
    return uuidv4();
}

const checkChatTextValid = async (content) => {
    const regex = /# (.*)/gm;
    const checkUrl = await axios.get(`https://v2.alapi.cn/api/censor/text?token=${config.alApiValidAsV2Token}&text=${content.replace(regex, '$1')}`);
    if (!checkUrl) return true;
    return checkUrl.data;
};

const extractIPv4FromIPv6 = (ipString) => {
    const parts = ipString.split("::ffff:");
    return parts.length > 1 ? parts[1] : ipString;
}

module.exports = { returnServerToken, returnThisTime, writeRunningLog, checkChatTextValid, extractIPv4FromIPv6 }
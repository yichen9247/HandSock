
/* eslint-disable no-undef */

import fs from 'fs';
import axios from 'axios';
import { styleText } from 'util';
import config from '../config.mjs';
import { v4 as uuidv4 } from 'uuid';
import dateTool from '../class/time.mjs';

export const returnThisTime = () => {
    const date = new dateTool().Date();
    return date.Year + "-" + date.Month + "-" + date.Date + " " + date.Hou + ":" + date.Min;
}

export const writeRunningLog = (content) => {
    console.log(styleText('grey', content));
    fs.appendFile('cache/running.log', content, () => { });
}

export const returnServerToken = () => {
    return uuidv4();
}

export const checkChatTextValid = async (content) => {
    const regex = /# (.*)/gm;
    const checkUrl = await axios.get(`https://v2.alapi.cn/api/censor/text?token=${config.alApiValidAsV2Token}&text=${content.replace(regex, '$1')}`);
    if (!checkUrl) return true;
    return checkUrl.data;
};

export const extractIPv4FromIPv6 = (ipString) => {
    const parts = ipString.split("::ffff:");
    return parts.length > 1 ? parts[1] : ipString;
}
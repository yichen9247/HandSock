/* eslint-disable no-undef */

import axios from "axios";

const robotChat = async (command) => {
    if (command === "随机一言") {
        try {
            const hitokoto = await axios.get('https://international.v1.hitokoto.cn/');
            return hitokoto.data.hitokoto;
        } catch (e) {
            return "机器人消息发送失败！";
        }
    }

    if (command === "你好世界") {
        return command;
    }
}

export default { robotChat };
/* eslint-disable no-undef */

import redis from "../redis.mjs";
import { styleText } from 'util';
import { createClient } from "redis";

let isConnectionEstablished = false;
const redisClient = createClient(redis);

const establishConnection = async () => {
    if (isConnectionEstablished) return;
    try {
        await redisClient.connect();
    } catch (error) {
        console.log(styleText('red', '[SERVER]：Failed to connect to Redis Server!'));
    }
};

const closeConnection = async () => {
    await redisClient.quit();
    isConnectionEstablished = false;
    console.log(styleText('green', '[SERVER]：Redis connection closed.'));
};

redisClient.on('connect', () => {
    isConnectionEstablished = true;
    console.log(styleText('green', '[SERVER]：Connected to the Redis server!'));
});

redisClient.on('error', (error) => {
    closeConnection();
    isConnectionEstablished = false;
    console.log(styleText('red', `[SERVER]：REDIS ERROR：${error}`));
});

export const setRedisIPCatch = async (address) => {
    try {
        await establishConnection();
        const status = await redisClient.get(address);
        if (status !== "True") {
            await redisClient.set(address, 'True');
            await redisClient.expire(address, 3);
        }
        return status === "True";
    } catch (error) {
        closeConnection();
        isConnectionEstablished = false;
        console.log(styleText('red', `[SERVER]：REDIS ERROR：${error}`));
    }
};

export const cleanup = async () => await closeConnection();
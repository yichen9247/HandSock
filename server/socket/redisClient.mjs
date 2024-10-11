/* eslint-disable no-undef */

import redis from "../redis.mjs";
import { styleText } from 'util';
import { createClient } from "redis";

const redisClient = createClient(redis);

export const connectToRedisServer = async () => {
    (async () => {
        try {
            await redisClient.connect();
        } catch (err) {
            console.log(styleText('red', '[SERVER]：Failed to connect to Redis Server!'));
        }
    })();

    redisClient.on('connect',async () => {
        console.log(styleText('green', '[SERVER]：Server：Connected to the Redis server!'));
    });

    redisClient.on('error',async (error) => {
        await redisClient.quit();
        console.log(styleText('red', `[SERVER]：REDIS ERROR：${error}`));
    });
}

export const setRedisIPCatch = async (address) => {
    try {
        await connectToRedisServer();
        let status = await redisClient.get(address);
        if (status !== "True") {
            await redisClient.set(address, 'True');
            await redisClient.expire(address, 3);
            return false;
        }
        return status === "True";
    } catch (error) {
        console.log(styleText('red', `[SERVER]：REDIS ERROR：${error}`));
        return false;
    } finally {
        await redisClient.quit();
    }
}

/* eslint-disable no-undef */

const mysql = require('mysql');
const config = require('../config');
const database = require('../database');
const { styleText } = require('node:util');
const connection = mysql.createConnection(database);

if (!config.connectionFaild) {
    connection.connect((error) => {
        if (error) {
            console.log(styleText('red', '[CHECK]：Failed to connect to MySQL database!'));
            return process.exit(1);
        }
        console.log(styleText('green', '[CHECK]：Connected to the MySQL server!'));
    
        connection.query('SHOW TABLES LIKE "history"', (error, result) => {
            if (error) return process.exit(1);
            if (result.length === 0) {
                console.log(styleText('yellow', '[CHECK]：Detect Mysql table is not exists!'));
                console.log(styleText('yellow', '[CHECK]：Create Mysql table operation will be performed!'));
                
                connection.query('CREATE TABLE history (chatData LONGTEXT)', (error) => {
                    if (error) return process.exit(1);
                    console.log(styleText('green', '[CHECK]：Create Mysql table is success!'));
                    const historyText = JSON.stringify(["{\"code\":0,\"name\":\"system\",\"message\":\"null\",\"userid\":\"0\",\"userqq\":0,\"channel\":\"0\",\"time\":\"2023-12-24 13:27\"}"]);
                    const query = 'INSERT INTO history (chatData) VALUES (?)';  
                    connection.query(query, historyText, (error) => {  
                        if (error) return process.exit(1);
                        console.log(styleText('green', '[CHECK]：Insert Virtual data into Mysql table is success!'));
                        process.exit(0);
                    });
                });
            } else {
                console.log(styleText('green', '[CHECK]：Detect Mysql table is already exists!'));
                connection.query('SELECT chatData FROM history', (error, results) => {  
                    if (error) return process.exit(1);
                    if (results.length === 0) {
                        console.log(styleText('yellow', '[CHECK]：Detect Mysql table is not anything in!'));
                        console.log(styleText('yellow', '[CHECK]：Insert Virtual data into Mysql table operation will be performed!'));
                        const historyText = JSON.stringify(["{\"code\":0,\"name\":\"system\",\"message\":\"null\",\"userid\":\"0\",\"userqq\":0,\"channel\":\"0\",\"time\":\"2023-12-24 13:27\"}"]);
                        const query = 'INSERT INTO history (chatData) VALUES (?)';  
                        connection.query(query, historyText, (error) => {  
                            if (error) return process.exit(1);
                            console.log(styleText('green', '[CHECK]：Insert Virtual data into Mysql table is success!'));
                            process.exit(0);
                        });
                    } else process.exit(0);
                });
            }
        });
    });
} else process.exit(1);
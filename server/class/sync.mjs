/* eslint-disable no-undef */

import fs from 'fs';
import mysql from 'mysql';
import { styleText } from 'util';
import database from '../database.mjs';

const connection = mysql.createConnection(database);

connection.connect((error) => {  
  if (error) {
      console.log(styleText('red', '[CHECK]：Failed to connect to MySQL database!'));
      return process.exit(1);
  }
  console.log(styleText('green', '[SYNC]：Connected to the MySQL server!'));
    
  connection.query('SHOW TABLES LIKE "users"', (error, result) => {
      if (error) throw error;
      if (result.length === 0) {
        connection.query('CREATE TABLE users (userid BIGINT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255), userqq BIGINT)', () => {  
          if (error) throw error;
        });
      }
  });
  
  connection.query('SHOW TABLES LIKE "history"', (error, result) => {
    if (error) throw error;
    if (result.length === 0) {
      connection.query('CREATE TABLE history (chatData LONGTEXT)', (error) => {  
          if (error) throw error;
          const historyText = JSON.stringify(["{\"code\":0,\"name\":\"system\",\"message\":\"null\",\"userid\":\"0\",\"userqq\":0,\"channel\":\"0\",\"time\":\"2023-12-24 13:27\"}"]);
          const query = 'INSERT INTO history (chatData) VALUES (?)';  
          connection.query(query, historyText, (error) => {  
              if (error) throw error;
          });
      });
    }
  });
});

const databaseSYncWithLocation = () => {
  connection.query('SELECT chatData FROM history', (error, results) => {  
      if (error) throw error;
      fs.writeFile('cache/history.json',results[0].chatData,(error) => {
          if (error) throw error;
      });
  });
}

export default { databaseSYncWithLocation: databaseSYncWithLocation }
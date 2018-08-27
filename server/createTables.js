/*
const mysql = require('mysql');

const connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '1234',
    database: 'Language',
    port: '3306',
});
connection.connect();

user_info= 'create table user_info (u_id VARCHAR(20) PRIMARY KEY, ' +
    'password varchar(20) NOT NULL);'
public_dic= 'create table public_dic (word VARCHAR(20) PRIMARY KEY);'

user='user1';
private_dic= 'create table '+ `${user}_word`+ '(word VARCHAR(20) );'
record= 'create table '+ `${user}_record`+ '(time DATETIME, word VARCHAR(20));'

connection.query(user_info, (err, res, fields)=>{
    if(err) throw err;
    console.log("user_info");
});

connection.query(public_dic, (err, res, fields)=>{
    if(err) throw err;
    console.log("pd");
});

connection.query(private_dic, (err, res, fields)=>{
    if(err) throw err;
    console.log("pri d");
});

connection.query(record, (err, res, fields)=>{
    if(err) throw err;
    console.log("recor");
});



connection.end();

*/
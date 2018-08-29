//
// const mysql = require('mysql');
const express= require('express'),http  = require('http');

const app=express();

/*
const connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '1234',
    database: 'Language',
    port: '3306',
});

connection.connect();

app.use((req,res,next)=>{
    getdata='select * from user_info';
    connection.query( getdata, (err, rows, fields)=>{
        if(err) throw err;
        console.log(req.query);

        dictionary=JSON.stringify(rows);
        console.log(dictionary);

        res.end(dictionary);
    });
});
*/



app.use((req,res,next)=>{
    console.log(req.query);
    res.end("connected");
});



//connection.end();

app.listen(3000, ()=>{
    console.log('server started');
});
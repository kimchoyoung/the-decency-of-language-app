const mysql = require('mysql');
const express= require('express'),http  = require('http');

const app=express();
let version=1;

const connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'epik1m1s',
    database: 'language',
    port: '3306',
});
connection.connect();

app.use('/', (req,res,next)=>{
    res.send(version);
})

app.use('/sign_up',(req,res,next)=>{
    let name = req.query.name
    let id = req.query.id
    let password= req.query.password

    query=`insert into user_info (user_id, user_name, user_pw) values ('${name}','${id}','${password}' );`;
    connection.query( query, (err, rows, fields)=>{
        if(err) {
            res.send("동일한 ID가 존재하거나 잘못된 양식입니다.")
            throw err;
        }

        result=JSON.stringify(rows);
        console.log(result);
        res.end("회원가입 완료");
    });
});

app.use('/login',(req,res,next)=>{
    let id= req.query.id;
    let password= req.query.password

    query=`select * from user_info where user_id= '${id}' and user_pw='${password}'`
    connection.query( query, (err, rows, fields)=>{
        if(err) throw err;

        result=JSON.stringify(rows);
        console.log(result);

        if(rows==undefined)
            res.end('false')
        else
            res.end('true')
    });
});

app.use('/update',(req,res,next)=>{
    query=`select * from dictionary`
    connection.query(query, (err,rows,fields)=>{
        if(err) throw err;

        let dictionary=JSON.stringify(rows);
        res.send(dictionary);
    })
})


/*
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
//connection.end();

app.listen(3000, ()=>{
    console.log('server started');
});

const mysql = require('mysql');
const express= require('express'),http  = require('http');

const app=express();


const connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'epik1m1s',
    database: 'language',
    port: '3306',
});

connection.connect();

app.use('/sign_up',(req,res,next)=>{

    let name = req.query.name
    let id = req.query.id
    let password= req.query.password

    query=`insert into user_info (user_id, user_name, user_pw) values ('${name}','${id}','${password}' );`;

    connection.query( query, (err, rows, fields)=>{
        if(err) throw err;

        result=JSON.stringify(rows);
        console.log(result);

        res.end("회원가입 완료");
    });
});


app.use('/login',(req,res,next)=>{

    let id= req.query.id;
    let password= req.query.password

    query=`select * from user_info where user_id= ${id} and user_pw=${password}`


    connection.query( query, (err, rows, fields)=>{
        if(err) throw err;

        dictionary=JSON.stringify(rows);
        console.log(result);

        res.end("로그인 완료")
    });
});

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
<<<<<<< HEAD
*/



app.use((req,res,next)=>{
    console.log(req.query);
    res.send("connected");
});


=======
>>>>>>> a93ff7543edbc37174e9d17a2c667f507276b659

*/
//connection.end();

app.listen(3000, ()=>{
    console.log('server started');
});

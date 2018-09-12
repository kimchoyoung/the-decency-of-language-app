const mysql = require('mysql');
const express= require('express'),http  = require('http');

const app=express();

const connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '0000',
    database: 'language',
    port: '3306',
});
connection.connect();

/*
app.use('/', (req,res,next)=>{
    let version='1';
    res.send(version);
    next();
})
*/

app.use('/sign_up',(req,res,next)=>{
    let name = req.query.name
    let id = req.query.u_id
    let password= req.query.u_pwd

    //query=`insert into user_info (user_id, user_name, user_pw) values ('${id}','${name}','${password}' );`;
    query=`insert into user_info (u_id, password) values (${id},${password} );CREATE TABLE IF NOT EXISTS ${id} user_rec( word varchar(255), time timestamp default current_timestamp );`;

    connection.query( query, (err, rows, fields)=>{
        if(err) {
            res.send("false") // 동일한 아이디 존재
        } else {
            result = JSON.stringify(rows);
            console.log(result);
            res.end("true");
        }
    });
});

app.use('/test', (req,res,next)=>{
    query= "select * from user_info;"
    connection.query( query, (err, rows, fields)=>{
        if(err) throw err;

        result=JSON.stringify(rows);
        console.log(result);
    });
});

app.use('/login',(req,res,next)=>{
    let id= req.query.u_id;
    let password= req.query.u_pwd;
    console.log(id, password)

    query=`select * from user_info where u_id =${id} AND password = ${password};`
    // query=`select * from user_info where u_id collate latin1_swedish_ci =${id} AND password = ${password};`

    connection.query( query, (err, rows, fields)=>{
        console.log(rows);

        if(rows == undefined || rows.length == 0) {
            res.send('False')
            console.log('false')
        }
        else {
            res.send('True')
            console.log('true')
        }
    });
});

app.use('/id_check',(req,res,next)=>{
    let id= req.query.u_id;
    console.log(id)

    query=`select * from user_info where u_id=${id};`

    connection.query( query, (err, rows, fields)=>{
		console.log(rows);
        if(rows == undefined || rows.length == 0) {
            res.send('True')
            console.log('true')
        }
        else {
            res.send('False')
            console.log('false')
        }
    });
});

var DBversion='3'

app.use('/version_check',(req,res,next)=>{
    let version = req.query.version
    console.log('version == ' + DBversion);
    res.send(DBversion)
})

app.use('/update',(req,res,next)=>{
    console.log('update');
    
    query =`select * from dictionary;`
        connection.query(query, (err, rows, fields)=>{
            // console.log(rows)
            words=JSON.stringify(rows)
            res.send(words)
            // console.log(words)
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
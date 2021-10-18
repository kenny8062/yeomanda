const io = require( "socket.io" )();
const socketapi = {
    io: io
};

const atob = require('atob')
const AWS = require('aws-sdk')
const chatConfig = require('./config/aws/Chat')
const fcmConfig = require('./config/aws/FcmToken')

function parseJwt (token) {
  var base64Url = token.split('.')[1];
  var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
  }).join(''));

  return JSON.parse(jsonPayload);
};

const socketApi = require('./socketApi');
const redis = require('socket.io-redis'); // redis의 publish/subscribe 기능
io.adapter(redis({
  host : 'localhost',
  port : 6379
}))

/**
 * redis cache
 */
const redis_client = require('redis');
const redisPort = 6379
const client= redis_client.createClient({
  port:6379,
  host:'localhost'
});

AWS.config.update(chatConfig.aws_iam_info);
const docClient = new AWS.DynamoDB.DocumentClient();

/**
 * connection to aws mysql server
 */
const mysql = require("mysql2/promise");
const conn = require('./config/aws/Travelers');

/**
 * fcm alarm
 */
const admin = require('firebase-admin')
const serAccount = require('./config/yeomanda-project-firebase-adminsdk-fryso-e71f4fcb25.json')
admin.initializeApp({
  credential: admin.credential.cert(serAccount),
})

io.on('connection', async function(socket){
	console.log(`made socket connected !!! , ${socket.id}`);
  // receive: on / send: emit
	socket.on('chatRoom', function(data){
    /**
     * data - room_id
     */
    const room_id = data.room_id // room name
    socket.join(room_id) 

    /**
     * find tokens
     */
    
    
    // 클라이언트에게 보낼 메세지
    const res = {'res' : "success to enter the chat room"} // json 형태로 보내야 한다. 
		io.emit('chatRoom', res);
	});


  socket.on('message', async function(data){
    /**
    * data - room_id, token, content
    */
    const nowDate = new Date();
    const token = socket.token = data.token
    const content = socket.content = data.content
    const room_id = socket.room_id = data.room_id
    const sender = parseJwt(token).email
    const name = parseJwt(token).name
    const sendTime = nowDate.toDateString() + ' ' + nowDate.toTimeString().split(' ')[0]

    const res = {
      'message' : content,
      'senderEmail' : sender,
      'time' : sendTime,
      'senderName' : name
    }
    io.to(room_id).emit('message', res)
    /**
     * try to store in redis cache --> fail
     * so, 디비에 바로바로 저장하고 알람보내기 
     */
    const newChat = {
      "createdAt" : sendTime,
      "senderEmail" : sender,
      "content" : content,
      "senderName" : name
    }

    const params_to_find_chatroom = {
        TableName : chatConfig.aws_table_name,
        KeyConditionExpression: 'room_id = :i',
        ExpressionAttributeValues: {
            ':i' : room_id
        }   
    };
    const chatRoom = await docClient.query(params_to_find_chatroom).promise()
    const newMessage = chatRoom.Items[0].chatMessages
    const users = chatRoom.Items[0].members // fcm alarm 보내기 위해서 그 방 사람들 이메일을 찾아야 해
    
    newMessage.push(newChat)
    const params_to_put_message = {
      TableName : chatConfig.aws_table_name,
      Item : {
          "room_id" : room_id,
          "members" : chatRoom.Items[0].members,
          "teams" : chatRoom.Items[0].teams,
          "chatMessages" : newMessage
      } 
    };
    
    
    try{
      const resultChat = await docClient.put(params_to_put_message).promise()
      // 이제 여기서 알람을 보내야 해 
        /**
         * 기존에 디비에 있는 데이터 읽어오는 작업 -> 필요해.
         */
      users.filter( async(u) => {
        if(u != sender){
          console.log(u)
          const connection = await mysql.createConnection(conn.db_info);
          const sql = `select * from fcm_token where email = '${u}';` 
          const result_sql = await connection.query(sql)
          // const params_to_find_token = {
          //   TableName : fcmConfig.aws_table_name,
          //   KeyConditionExpression: 'email = :i',
          //   ExpressionAttributeValues: {
          //       ':i' : u
          //   }   
          // }; 
          // const fcmToken = await docClient.query(params_to_find_token).promise()
          const target_token = result_sql[0][0].token 
          const message = {
            data: {
              title: '채팅 도착',
              body: '새로운 메세지가 도착하였습니다.'
            },
            token: target_token,
          }
          admin
            .messaging()
            .send(message)
            .then(function (response) {
              console.log('Successfully sent message: : ', response)
            })
            .catch(function (err) {
              console.log('Error Sending message!!! : ', err)
            })
        }
        
      })
      // client.sadd(room_id, JSON.stringify(newChat))
      
    }catch(err){
      console.log(err)
    }

  
  })
  // 클라이언트와 연결 해제
  socket.on('disconnect', async() => {
    io.emit('updateMessage', "연결이 끊어졌습니다.");

    console.log(`made socket disconnected !!! : ${socket.id}`)
  })
});

module.exports = socketapi;
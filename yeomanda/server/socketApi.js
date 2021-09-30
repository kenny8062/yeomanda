const io = require( "socket.io" )();
const socketapi = {
    io: io
};

// Add your socket.io logic here!
// io.on( "connection", function( socket ) {
//     console.log( "A user connected" );
// });
// end of socket.io logic
const atob = require('atob')
const AWS = require('aws-sdk')
const chatConfig = require('./config/aws/Chat')

function parseJwt (token) {
  var base64Url = token.split('.')[1];
  var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
  }).join(''));

  return JSON.parse(jsonPayload);
};

const socketApi = require('./socketApi');
const redis = require('socket.io-redis');
io.adapter(redis({
    host : 'localhost',
    port : 6379
}))


io.on('connection', async function(socket){
	console.log(`made socket connected !!! , ${socket.id}`);
  // receive: on / send: emit
	socket.on('chatRoom', function(data){
    /**
     * data - room_id
     */
    const room_id = data.room_id // room name
    socket.join(room_id) 
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
    const sendTime = nowDate.toString()

    const res = {
      'message' : content,
      'senderEmail' : sender,
      'time' : sendTime,
      'senderName' : name
    }
    console.log(res)
    io.to(room_id).emit('message', res)
    /**
     * store message to db
     */
    try{
      AWS.config.update(chatConfig.aws_iam_info);
      const docClient = new AWS.DynamoDB.DocumentClient();
  
      /**
       * find email in FAVORITES table and delete favorite team_no
       */
      const params_to_find_chatroom = {
          TableName : chatConfig.aws_table_name,
          KeyConditionExpression: 'room_id = :i',
          ExpressionAttributeValues: {
              ':i' : room_id
          }   
      };
      const chatRoom = await docClient.query(params_to_find_chatroom).promise()
      
      const newChat = {
          "createdAt" : sendTime,
          "senderEmail" : sender,
          "content" : content,
          "senderName" : name
      }
  
      /**
       * 여기서 부터 이제 기존에 있던 메세지들 읽어와서 새로 업데이트 하고 저장하는 과정
       */
      const newMessage = []
      chatRoom.Items[0].chatMessages.filter( m => {
          newMessage.push(m)
      })
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
      const resultChat = await docClient.put(params_to_put_message).promise()
    }catch(err){
      console.log(err)
    }

    
  })
  // 클라이언트와 연결 해제
  socket.on('disconnect', () => {
      io.emit('updateMessage', "연결이 끊어졌습니다.");
      console.log(`made socket disconnected !!! : ${socket.id}`)
  })
});

module.exports = socketapi;
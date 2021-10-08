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

const redis_client = require('redis');
// const rejson = require('redis-rejson');
// rejson(redis_client)

const redisPort = 6379
const client= redis_client.createClient({
  port:6379,
  host:'localhost'
});

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
    io.to(room_id).emit('message', res)
    /**
     * try to store in redis cache
     */
    try{
      // 문제점 - 스트링으로 append해서 저장하면 나중에 split 해야 하는데 그 기준은...?
      const newChat = {
        "createdAt" : sendTime,
        "senderEmail" : sender,
        "content" : content,
        "senderName" : name
      }
      client.sadd(room_id, JSON.stringify(newChat))
      
    }catch(err){
      console.log(err)
    }

  
  })
  // 클라이언트와 연결 해제
  socket.on('disconnect', async() => {
    io.emit('updateMessage', "연결이 끊어졌습니다.");

    // client.smembers('newChat', async(err, data) => {
    //   data.filter(d => {
    //     console.log(JSON.parse(d))
    //   })
    // })    
    console.log(`made socket disconnected !!! : ${socket.id}`)
    /**
     * flush all cache
     */
    //client.flushall("ASYNC")
  })
});

module.exports = socketapi;
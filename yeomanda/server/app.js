var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');

var app = express();

// socket 1 
app.io = require('socket.io')();
//var io = socket(server);


// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/users', usersRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});


/**
 * 
 * 
 * SOCKET !!!!!
 * 
 */
//const verifyToken = require('./middlewares/verifyToken');

// client 측에서 connect() 함수를 실행하면 저절로 connection 이라는 이벤트가 생성되어 서버와 연결이 된다. 
// 콜백함수의 파라미터 socket은 현재 서버와 연결된 대상을 가리킨다.
// socket.id는 접속할 때 마다 계속 변함 -> 고유 값으로 사용하기 어려울 듯.
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


app.io.on('connection', async function(socket){
	console.log(`made socket connected !!! , ${socket.id}`);
  // receive: on / send: emit
	socket.on('chatRoom', function(data){
    /**
     * data - room_id
     */
    const room_id = data.room_id // room name
  
    // 클라이언트에게 보낼 메세지
    const res = {'res' : "success to enter the chat room"} // json 형태로 보내야 한다. 
		app.io.emit('chatRoom', res);
	});


  socket.on('message', async function(data){

    /**
     * data - room_id, token, content
     */
    const token = socket.token = data.token
    const content = socket.content = data.content
    const room_id = socket.room_id = data.room_id
    const sender = parseJwt(token).email
    const sendTime = Date.now()
    socket.join(room_id) 

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
          "sender" : sender,
          "content" : content
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

    const res = {
      'message' : content,
      'sender' : sender,
      'time' : sendTime
    }
    console.log(res)
    app.io.to(room_id).emit('message', res)
  })
  // 클라이언트와 연결 해제
  socket.on('disconnect', () => {
      app.io.emit('updateMessage', "연결이 끊어졌습니다.");
      console.log(`made socket disconnected !!! : ${socket.id}`)
  })
});


module.exports = app;
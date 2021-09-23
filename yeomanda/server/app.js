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
function parseJwt (token) {
  var base64Url = token.split('.')[1];
  var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
  }).join(''));

  return JSON.parse(jsonPayload);
};


const AWS = require('aws-sdk')
const chatConfig = require('./config/aws/Chat')

// 클라이언트에서 받는 메세지를 디비에 저장하는 함수
const storeMessage = async(data) => {
  AWS.config.update(chatConfig.aws_iam_info);
  const docClient = new AWS.DynamoDB.DocumentClient();
  const params_to_new_chat = {
    TableName : chatConfig.aws_table_name,
    Item : {
        room_id : chatRoomId,
        members : chatterList,
        teams : uniqueArr
    }
  };  
  const result = await docClient.put(params_to_new_chat).promise()
}

app.io.on('connection', async function(socket){
	console.log(`made socket connected !!! , ${socket.id}`);
  var room_id = 0
  var token = 0
  var sender = 0
  // newUser1 이라는 이벤트를 통해 데이터를 주고 받을 수 있다.
  // receive: on / send: emit
	socket.on('chatRoom', function(data){
    /**
     * data - room_id, token
     */
    room_id = data.room_id
    token = data.token
    sender = parseJwt(token).email


    console.log({room_id, token})
		console.log('-----------------')
    console.log(parseJwt(token))

    // 클라이언트에게 보낼 메세지
    const res = {'res' : "success to enter the chat room"} // json 형태로 보내야 한다. 
		app.io.emit('chatRoom', res);
	});

  socket.on(room_id, async function(data){
    console.log(data)
    /**
     * data - content
     */
    const content = data
    AWS.config.update(chatConfig.aws_iam_info);
    const docClient = new AWS.DynamoDB.DocumentClient();

    /**
     * 1. find email in FAVORITES table and delete favorite team_no
     */
    const params_to_find_chatroom = {
        TableName : chatConfig.aws_table_name,
        KeyConditionExpression: 'room_id = :i',
        ExpressionAttributeValues: {
            ':i' : room_id
        }   
    };
    const chatRoom = await docClient.query(params_to_find_chatroom).promise()
    console.log(chatRoom)

    const newChat = {
        "createdAt" : Date.now(),
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

    // 클라이언트에게 보낼 메세지
    const temp = {'res' : "success to send message"} // json 형태로 보내야 한다. 
		app.io.emit(room_id, temp);
  })


  // 클라이언트와 연결 해제
  socket.on('disconnect', () => {
      app.io.emit('updateMessage', "newUser1이 나갔습니다. ");
      console.log(`made socket disconnected !!! : ${socket.id}`)
  })
});


module.exports = app;
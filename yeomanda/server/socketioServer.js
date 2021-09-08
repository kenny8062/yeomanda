const express = require('express');
const socket = require('socket.io');
const {spawn} = require('child_process');

// App Setup
var app = express();
// Create server and listen to specific port number
var server = app.listen(4000, function(){
	console.log('Listening to requests on port 4000');
});

// Socket setup; provide server reference to work with
var io = socket(server);

// Listening to an connection event; socket parameter refers to created/particular socket
io.on('connection', function(socket){
	// Print socket id on new connection
	console.log(`made socket connection, ${socket.id}`);

	// Listen for chat message being sent from client
	socket.on('newUser1', function(data){
		console.log('-----------------')
		console.log(data)
        

		// Send received chat message to all connected clients
		io.sockets.emit('updateMessage', "newUser1이 접속하였습니다.");
	});

    socket.on('disconnect', () => {
        socket.broadcast.emit('updateMessage', "newUser1이 나갔습니다. ");
        console.log(`Socket disconnected : ${socket.id}`)
    })
});
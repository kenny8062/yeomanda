const util = require('../modules/util');
const fs = require('fs')
const path = require('path');
const responseMessage = require('../modules/responseMessage');
const statusCode = require('../modules/statusCode');
const stream = require('stream')


// dynamodb
const AWS = require('aws-sdk')
const favoriteConfig = require('../config/aws/Favorite')
const userConfig = require('../config/aws/User')
const chatConfig = require('../config/aws/Chat')

// rds mysql
const mysql = require("mysql2/promise");
const conn = require('../config/aws/Travelers');


// s3 getObject
const s3 = new AWS.S3({
    accessKeyId: userConfig.aws_iam_info.accessKeyId,
    secretAccessKey: userConfig.aws_iam_info.secretAccessKey,
    region : 'ap-northEast-2'
});

// delay function 
const sleep = (ms) => {
    return new Promise(resolve=>{
        setTimeout(resolve,ms)
    })
}


const inToChatRoom = async(req, res) => {
    try{
        const userEmail = req.decoded.email
        const otherTeamNum = req.params.otherTeamNum // todo: 그에 맞는 팀 이름이 없는 예외 처리도 해야한다. 
        /**
         * 1. 채팅하려고 하는 여행객이 속한 팀의 번호를 추출.
         */
    
        const connection = await mysql.createConnection(conn.db_info);
        const sql = `select team_no from travel_with where email='${userEmail}' and isfinished = '0';` 
        const result = await connection.query(sql)
        const userTeamNum = result[0][0].team_no
    
        // 3_7 (o) , 7_3 (x)
        const chatRoomId = (userTeamNum >= otherTeamNum) ? otherTeamNum + '_' + userTeamNum : userTeamNum + '_' + otherTeamNum;
    
        /**
         * 2. 이제 디비를 둘러보면서 해당 Id 가 있는지 없는지 확인
         */
        AWS.config.update(chatConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();
    
        /**
         * 1. find email in FAVORITES table and delete favorite team_no
         */
        const params_to_find_chatroom = {
            TableName : chatConfig.aws_table_name,
            KeyConditionExpression: 'room_id = :i',
            ExpressionAttributeValues: {
                ':i' : chatRoomId
            }   
        };
        const chatRoom = await docClient.query(params_to_find_chatroom).promise()
        
        // 처음 채팅한다 -> 방을 만들어줘야 한다.
        // 방을 만들기 위해 / 1.방id 2.멤버들 이 필요해
        if(chatRoom.Items.length === 0){
            const chatterList = []
            const teamList = []
            const sql_to_sender = `select email, team_name from travel_with where team_no='${userTeamNum}' and isfinished = '0';` 
            const result_sender = await connection.query(sql_to_sender)
            result_sender[0].filter(s => {
                chatterList.push(s.email)
                teamList.push(s.team_name)
            })
            const sql_to_receiver = `select email, team_name from travel_with where team_no='${otherTeamNum}' and isfinished = '0';` 
            const result_receiver = await connection.query(sql_to_receiver)
            result_receiver[0].filter(r => {
                chatterList.push(r.email)
                teamList.push(r.team_name)
            })
            const set = new Set(teamList);
            const uniqueArr = [...set];

            const params_to_new_chat = {
                TableName : chatConfig.aws_table_name,
                Item : {
                    room_id : chatRoomId,
                    members : chatterList,
                    teams : uniqueArr
                }
            };  
            const result = await docClient.put(params_to_new_chat).promise()
            return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.MAKE_NEW_ROOM, 
                {room_id : chatRoomId,
                    members : chatterList,
                    teams : uniqueArr}))
        }
        // 기존에 채팅방이 개설 되었을 경우.
        else{
            return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.ALREADY_ROOM, chatRoom.Items[0]))
        }
    }catch(err){
        console.log(err)
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
    

}

const getAllMyChatList = async(req, res) => {
    try{
        const userEmail = req.decoded.email
        const chatInfo = [] // 반환해야 할 정보들을 담는다.
        /**
         * 현재 이용자가 속한 채팅방의 chatroomID를 가져와서 다시 scan 하여
         * 1) 채팅방 id 
         * 2) 마지막 대화
         * 3) 상대 팀명
         * 반환
         */
    
        /**
         * chatroom table의 teams field에서 우리 팀 말고 다른 팀을 알아내기 위해 일단은 우리팀의 이름을 알아내야 해.
         */
        const connection = await mysql.createConnection(conn.db_info);
        const sql_to_find_teamName = `select team_name from travel_with where email='${userEmail}' and isfinished = '0';` 
        const result = await connection.query(sql_to_find_teamName)
        const myTeam = result[0][0].team_name // 우리팀 이름
    
        AWS.config.update(chatConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();
        const params_to_find_chatroom = {
            TableName : chatConfig.aws_table_name,
            FilterExpression: 'contains (members, :i)',
            ExpressionAttributeValues: {
                ':i' : userEmail
            }   
        };
        const chatRoom = await docClient.scan(params_to_find_chatroom).promise() // chatting tabled에서 현재 이용자가 속해 있는 모든 row들
        const roomList = []
        chatRoom.Items.filter(c => {
            chatInfo.push({"room_id" : c.room_id}) // 1)
            c.teams.filter(t => {
                if(t !== myTeam){
                    chatInfo.push({"otherTeamName" : t}) // 3)
                }
            })
            console.log(c.messages.slice(-1).pop())
            console.log(c.messages)

            // 만약에 아예 대화를 한적이 없어서 null 인 경우...?
            if(c.messages === undefined){
                console.log('yyyy')
                chatInfo.push({"message" : null})
                //return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.NO_MESSAGES))
            }else{
                chatInfo.push({"messages" : c.messages.slice(-1).pop()})
            }
        })
        return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.QUERY_SUCCESS, chatInfo))
    }catch(err){
        console.log(err)
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
    
    
}


module.exports = {
    inToChatRoom,
    getAllMyChatList
}

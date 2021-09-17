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
            const sql_to_sender = `select email from travel_with where team_no='${userTeamNum}' and isfinished = '0';` 
            const result_sender = await connection.query(sql_to_sender)
            result_sender[0].filter(s => {
                chatterList.push(s.email)
            })
            chatterList.push("|") // 팀 구분하기 위해
            const sql_to_receiver = `select email from travel_with where team_no='${otherTeamNum}' and isfinished = '0';` 
            const result_receiver = await connection.query(sql_to_receiver)
            result_receiver[0].filter(r => {
                chatterList.push(r.email)
            })
    
            const params_to_new_chat = {
                TableName : chatConfig.aws_table_name,
                Item : {
                    room_id : chatRoomId,
                    members : chatterList
                }
            };  
            const result = await docClient.put(params_to_new_chat).promise()
            return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.MAKE_NEW_ROOM, 
                "새로운 채팅 시작"))
        }else{
            return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.ALREADY_ROOM, 
                chatRoom))
        }
    }catch(err){
        console.log(err)
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
    

}

const getAllMyChatList = async(req, res) => {

}


module.exports = {
    inToChatRoom,
    getAllMyChatList
}
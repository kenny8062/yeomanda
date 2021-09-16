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

    
}

const getAllMyChatList = async(req, res) => {

}


module.exports = {
    inToChatRoom,
    getAllMyChatList
}
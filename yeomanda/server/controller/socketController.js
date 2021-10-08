const util = require('../modules/util');
const responseMessage = require('../modules/responseMessage');
const statusCode = require('../modules/statusCode');

const utils = require('util') 
const redis_client = require('redis')
const client = redis_client.createClient({
    port:6379,
    host:'localhost'
});
// dynamodb
const AWS = require('aws-sdk')
const chatConfig = require('../config/aws/Chat');



const caching = async(req, res) => {
    try{
        const {room_id} = req.body
         
        AWS.config.update(chatConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();

        const params_to_find_chatroom = {
            TableName : chatConfig.aws_table_name,
            KeyConditionExpression: 'room_id = :i',
            ExpressionAttributeValues: {
                ':i' : room_id
            }   
        };
        /**
         * 기존에 디비에 있는 데이터 읽어오는 작업 -> 필요해.
         */
        const chatRoom = await docClient.query(params_to_find_chatroom).promise()
        const newMessage = chatRoom.Items[0].chatMessages
        // chatRoom.Items[0].chatMessages.filter( m => {
        //     newMessage.push(m)
        // })

        /**
         * 캐시에 저장되어 있는 chatMessages 항목 읽어오는 작업
         */
        client.smembers(room_id, async(err, data) => {
            if(data.length){
                data.filter((d)=> {
                    const temp = JSON.parse(d)
                    newMessage.push(temp)
                    
                })
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
                return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.UPDATE_MESSAGE))
            }else{
                return res.status(statusCode.OK).send(util.fail(statusCode.NO_CONTENT, responseMessage.NO_MESSAGES))
            }
        })
        client.del(room_id) // 특정 캐시만 지우고 싶은데...
    }catch(err){
        console.log(err)
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
}

module.exports = {
    caching
}
const util = require('../modules/util');
const responseMessage = require('../modules/responseMessage');
const statusCode = require('../modules/statusCode');
// dynamodb
const AWS = require('aws-sdk')
const favoriteConfig = require('../config/aws/Favorite')

// rds mysql
const mysql_config = require('../config/aws/Travelers'); 
const conn = mysql_config.init()
mysql_config.connect(conn)


const favorite = async (req, res) => {
    const { email, favorite_team_no } = req.body
    AWS.config.update(favoriteConfig.aws_iam_info);
    const docClient = new AWS.DynamoDB.DocumentClient();

    const params_findFromUser = {
        TableName : favoriteConfig.aws_table_name,
        KeyConditionExpression: 'email = :i',
        ExpressionAttributeValues: {
            ':i' : email
        }   
    };
    const checkEmail_from_favorite = await docClient.query(params_findFromUser).promise()
    // case1 : favorite table에 이메일이 없는 경우 -> 처음 추가
    if(checkEmail_from_favorite.Items.length === 0){
        const accumulate_favorite_list = [] 
        accumulate_favorite_list.push(favorite_team_no)
        
        const params_to_push_favorite = {
            TableName : favoriteConfig.aws_table_name,
            Item : {
                email : email,
                favorite_team_no : accumulate_favorite_list
            }
        };
        docClient.put(params_to_push_favorite, function(err, data){
            if(err){
                return res.status(statusCode.INTERNAL_SERVER_ERROR).send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.QUERY_ERROR))
            } else {
                return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.ADD_FAVORITE))
            }
        })
    }
    // case2 : favorite table에 이메일이 있는 경우 -> 누적 추가
    else{
        const favorite_list = checkEmail_from_favorite.Items[0].favorite_team_no
        const accumulate_favorite_list = []
        for(var i=0; i<favorite_list.length; i++){
            accumulate_favorite_list.push(favorite_list[i])
        }
        accumulate_favorite_list.push(favorite_team_no)

        const params_to_accumulate_favorite = {
            TableName : favoriteConfig.aws_table_name,
            Item : {
                email : email,
                favorite_team_no : accumulate_favorite_list
            }
        };
        docClient.put(params_to_accumulate_favorite, function(err, data){
            if(err){
                return res.status(statusCode.INTERNAL_SERVER_ERROR).send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.QUERY_ERROR))
            } else {
                return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.ACCUMULATE_FAVORITE))
            }
        })
    
    }
}


module.exports = {
    favorite,
}
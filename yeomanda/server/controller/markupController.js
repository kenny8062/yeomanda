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
const mysql_config = require('../config/aws/Travelers'); 
const conn = mysql_config.init()
mysql_config.connect(conn)

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


const favorite = async (req, res) => {
    try{
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
        // 9/8 but, 이미 추가한 팀을 추가할 경우에는 예외 처리 해줘야 해 
        else{
            const favorite_list = checkEmail_from_favorite.Items[0].favorite_team_no
            const accumulate_favorite_list = []
            for(var i=0; i<favorite_list.length; i++){
                if(favorite_list[i] === favorite_team_no){
                    return res.status(statusCode.OK).send(util.fail(statusCode.BAD_REQUEST, responseMessage.ALREADY_ADDED, "이미 추가한 팀입니다."))
                }
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
    }catch(err){
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
    
}

/**
 * 이름 선택하면 해당 여행객의 상세 정보 반환 
 */
const userDetail = async (req, res) => {
    try{
        const { email } = req.body
        AWS.config.update(userConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();
        const params_to_find_userDetail = {
            TableName : userConfig.aws_table_name,
            KeyConditionExpression: 'email = :i',
            ExpressionAttributeValues: {
                ':i' : email
            }   
        };
    
        const checkEmail_from_user = await docClient.query(params_to_find_userDetail).promise()
        
        if(checkEmail_from_user.Items.length === 0){
            console.log("해당 이메일에 맞는 회원이 없습니다.")
            return res.status(statusCode.OK).send(util.fail(statusCode.NOT_FOUND, responseMessage.READ_USER_FAIL))
        }
        else{
            /**
             *  response url of s3 bucket 
             */
            const s3path = checkEmail_from_user.Items[0].files
            var userfacesURLlist = []
            for(var i=0; i<s3path.length; i++){
                const userfaceURL = 'https://yeomanda-userface.s3.ap-northeast-2.amazonaws.com/' + s3path[i];
                userfacesURLlist.push(userfaceURL)   
            }
    
            const userResult = {
                'email' : checkEmail_from_user.Items[0].email,
                'birth' : checkEmail_from_user.Items[0].birth,
                'sex' : checkEmail_from_user.Items[0].sex,
                'name' : checkEmail_from_user.Items[0].name,
                'files' : userfacesURLlist
            }
            return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.READ_USER_SUCCESS, userResult))
        }
    }catch(err){
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
    
}

const deleteFavorite = async(req, res) => {
    try{
        /**
         * 1. find email in FAVORITES table
         * 2. get list of favorites from given email
         * 3. get rid of deletedteam number from the list
         */
        const deletedTeam = req.params.team_no
        const deleter = req.decoded.email
        AWS.config.update(favoriteConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();
    
        const params_findFromUser = {
            TableName : favoriteConfig.aws_table_name,
            KeyConditionExpression: 'email = :i',
            ExpressionAttributeValues: {
                ':i' : deleter
            }   
        };
        const checkEmail_from_favorite = await docClient.query(params_findFromUser).promise()
        const newFavorites = checkEmail_from_favorite.Items[0].favorite_team_no // favorite list after delete
        
        for(var i in newFavorites){
            if(newFavorites[i].toString() === deletedTeam){
                if (i > -1) {
                    newFavorites.splice(i, 1); //   i 번째 위치하는 원소를 삭제함 -> 계속 삭제 하고 삭제 하다보면 배열의 상황이 다르기 때문에 이상한거를 삭제할 수도,...
                }               
            }
        }
        /**
         * get list from db and remove all and replace to new list
         */
        console.log(newFavorites)
        return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.DELETE_FAVORITE, newFavorites))
    }catch(err){
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
    
}

const showFavorites = async(req, res) => {
    try{

    }catch(err){
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
}
module.exports = {
    favorite,
    userDetail,
    showFavorites,
    deleteFavorite
}
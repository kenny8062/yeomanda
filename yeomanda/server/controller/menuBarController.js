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

/**
 * 즐겨찾기했던 팀들 보여주기
 * @param {*} req token
 * @param {*} res return
 * @returns like travelers/showTravlers?
 */


 const showFavorites = async(req, res) => {
    try{
        const userEmail = req.decoded.email
        AWS.config.update(favoriteConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();

        /**
         * 1. find email in FAVORITES table 
         */

         const params_find_favorites = {
            TableName : favoriteConfig.aws_table_name,
            KeyConditionExpression: 'email = :i',
            ExpressionAttributeValues: {
                ':i' : userEmail
            }   
        };
        const checkEmail_from_favorite = await docClient.query(params_find_favorites).promise()
        const Favorites = checkEmail_from_favorite.Items[0].favorite_team_no 

        /**
         * 2. get user's email from favorites list
         * 2-1. store team_no and email in list -> [(1, [a,b,c]), (2, [d,e,f]), ... ]
         */
        var team_no_emails_list = {} // 팀번호와 그에 해당되는 여행객들의 이메일을 같이 저장할 리스트.
        const emailList = []

        for(var i in Favorites){
            const sql = `select email from travel_with where team_no = '${Favorites[i]}';`
            conn.query(sql, async function(err, emails){
                if(err){
                    return res.status(statusCode.BAD_REQUEST).send(util.success(statusCode.OK, responseMessage.QUERY_ERROR, 
                        "fail to select email from travel_with where team_no = ?"))
                }else{
                    /**
                     * filter 로 이메일 데이터만 뽑아서 따로 저장
                     * async 하는 동안 for 문 다 돌았어 -> 그래서 마지막 팀이 계속 배열로 들어간다...
                     */
                    emails.filter( e => {
                        emailList.push(e.email)
                    })
                    console.log(emailList)
                    // console.log(Favorites[i])
                    // team_no_emails_list[Favorites[i]] = emailList
                    // console.log(team_no_emails_list)
                }
            })
        }
    }catch(err){
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
}

/**
 * 즐겨찾기 취소하기
 */
 const deleteFavorite = async(req, res) => {
    try{
        const deletedTeam = req.params.team_no
        const deleter = req.decoded.email
        AWS.config.update(favoriteConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();

        /**
         * 1. find email in FAVORITES table and delete favorite team_no
         */
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
                    newFavorites.splice(i, 1); 
                }               
            }
        }
        /**
         * 2. delete row of user email
         */
        const params_to_delete_favorite = { 
            TableName : favoriteConfig.aws_table_name, 
            Key: {
                "email" : deleter 
            },
        };

        docClient.delete(params_to_delete_favorite, function(err, data){
            if(err){
                return res.status(statusCode.OK).send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.QUERY_ERROR))
            } else {
                /**
                 * 3. delete success & push new favorite list that deleted a team_no
                 */
                const params_to_push_favorite = {
                    TableName : favoriteConfig.aws_table_name,
                    Item : {
                        email : deleter,
                        favorite_team_no : newFavorites
                    }
                };  
                docClient.put(params_to_push_favorite, function(err, data){
                    if(err){
                        return res.status(statusCode.INTERNAL_SERVER_ERROR).send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.QUERY_ERROR))
                    } else {
                        return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.DELETE_FAVORITE))
                    }
                })
            }
        })
    }catch(err){
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
    
}


const finishTravel = async(req, res) => {
    try{
        const finishTraveler = req.decoded.email
        /**
         * 1. find email in database that field 'isfinished=0' 
         * 2. change isfinished filed to 1
         */
         const sql = `select team_no from travel_with where email='${finishTraveler}' and isfinished = '0';` 
         conn.query(sql, async function(err, data){
             if(err){
                 console.log(err)
                 return res.status(statusCode.BAD_REQUEST).send(util.success(statusCode.OK, responseMessage.QUERY_ERROR, 
                    "fail to select team_no hopped to finish travel"))
             }else{
                const finishTeam = data[0].team_no
                const sql_to_finish = `update travel_with set isfinished = '1' where team_no = '${finishTeam}'`
                conn.query(sql_to_finish, async function(err, data){
                    if(err){
                        console.log(err)
                        return res.status(statusCode.BAD_REQUEST).send(util.success(statusCode.OK, responseMessage.QUERY_ERROR, 
                            "fail to update isfinished to 1"))
                    }else{
                        return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.QUERY_SUCCESS, 
                            "success to update isfinished to 1")) 
                    }
                })
             }
         })

    }catch(err){
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
}

module.exports = {
    showFavorites,
    deleteFavorite,
    finishTravel
}
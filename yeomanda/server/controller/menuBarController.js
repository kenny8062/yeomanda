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
const { constants } = require('buffer');

// s3 getObject
const s3 = new AWS.S3({
    accessKeyId: userConfig.aws_iam_info.accessKeyId,
    secretAccessKey: userConfig.aws_iam_info.secretAccessKey,
    region : 'ap-northEast-2'
});


/**
 * 즐겨찾기한 팀들의 팀 이름들을 보여줌.
 */
const showFavoriteTeamName = async(req, res) => {
    try{
        const connection = await mysql.createConnection(conn.db_info);
        const userEmail = req.decoded.email
        AWS.config.update(favoriteConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();
        const result = []  //  최종 결과

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
        const Favorites_no = checkEmail_from_favorite.Items[0] // 이용자가 즐겨찾기 한 팀들의 번호 리스트
        // 1. 즐겨찾기 한 팀들이 없을 경우
        if(!Favorites_no || Favorites_no.favorite_team_no.length == 0){
            return res.status(statusCode.OK).send(util.success(statusCode.fail, responseMessage.READ_USER_FAIL, result))                                        
        }
        /**
         * 2. 즐겨찾기 한 팀들이 있을 경우
         * 즐겨찾기 한 팀들의 번호 리스트를 가지고 travel_with 테이블에서 팀 이름들을 보내준다.
         */

        const Favorites = checkEmail_from_favorite.Items[0].favorite_team_no// 이용자가 즐겨찾기 한 팀들의 번호 리스트

        for(var i in Favorites){
            const sql = `select * from travel_plan where team_no = '${Favorites[i]}';`
            const data = await connection.query(sql)
                
            console.log(data[0])
            const teamNameList = []
            const teamNoList = []
            data[0].filter( e => { // data 에는 하나의 팀에 해당하는 여러 여행객들의 정보가 담겨져 있다. 
                teamNameList.push(e.team_name)
                teamNoList.push(e.team_no)
            })
            result.push({"team_name" : teamNameList[0], "member" : data[0].length, "team_no" : teamNoList[0]})
            if(result.length === Favorites.length){
                console.log(result)
                return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.QUERY_SUCCESS, result ))                                        
            }
        }
        await connection.end()

    }catch(err){
        console.log(err)
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
}

/**
 * 즐겨찾기 취소하기 - 즐겨찾기 리스트 보고 나서 취소할 수 있어.
 */
const deleteFavorite = async(req, res) => {
    try{
        const connection = await mysql.createConnection(conn.db_info);

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

/**
 * 여행 취소하기
 */
const finishTravel = async(req, res) => {
    try{
        const connection = await mysql.createConnection(conn.db_info);

        const finishTraveler = req.decoded.email
        /**
         * 1. find email in database that field 'isfinished=0' 
         * 2. change isfinished filed to 1
         */
        const sql = `select team_no from travel_plan where email='${finishTraveler}' and isfinished = '0';` 
        const result = await connection.query(sql)
             
        
        const finishTeam = result[0][0].team_no
        const sql_to_finish = `update travel_plan set isfinished = '1' where team_no = '${finishTeam}'`
        const data = await connection.query(sql_to_finish)  
        
        await connection.end()

        return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.QUERY_SUCCESS, 
            "success to update isfinished to 1")) 

    }catch(err){
        console.log(err)
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
}


const showFavoritesDetail = async(req, res) => {
    const connection = await mysql.createConnection(conn.db_info);

    const teamName = req.params.teamName
    const sql = `select email from travel_plan where team_name = '${teamName}';`
    const data = await connection.query(sql)
    
    await connection.end()

    const date = new Date()    
    const result = []
    data[0].filter( async(d) => {
        const user = d.email
        AWS.config.update(userConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();

        /**
         * 1. find email in FAVORITES table and delete favorite team_no
         */
        const params_findFromUser = {
            TableName : userConfig.aws_table_name,
            KeyConditionExpression: 'email = :i',
            ExpressionAttributeValues: {
                ':i' : user
            }   
        };
        const checkEmail = await docClient.query(params_findFromUser).promise()
        const file_root = 'https://yeomanda-userface.s3.ap-northeast-2.amazonaws.com/'
        const fileList = []
        for(var i in checkEmail.Items[0].files){
            fileList.push(file_root + checkEmail.Items[0].files[i])
        }
        console.log(checkEmail.Items[0].birth)
        const nowYear = date.getFullYear()
        const userInfo = {
            'email' : checkEmail.Items[0].email,
            'name' : checkEmail.Items[0].name,
            'sex' : checkEmail.Items[0].sex,
            'birth' : nowYear - checkEmail.Items[0].birth.substring(0,4) + 1,
            'files' : fileList
        }
        result.push(userInfo)
        if(result.length === data[0].length){
            return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.QUERY_SUCCESS, 
                result)) 
        }
    })
}

const getProfile = async(req, res) => {
    try{
        const userEmail = req.decoded.email
        AWS.config.update(userConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();
        const params = {
            TableName : userConfig.aws_table_name,
            KeyConditionExpression: 'email = :i',
            ExpressionAttributeValues: {
                ':i' : userEmail
            } 
        };
        const checkEmail_from_user = await docClient.query(params).promise()

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
    }catch(err){
        console.log(err);
        return res
            .status(statusCode.INTERNAL_SERVER_ERROR)
            .send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR));
    }
}

const updateProfile = async(req, res) => {
    try{
        const {email, updatedURI} = req.body
        console.log(updatedURI)
        /**
         * 1. 새로 이미지 저장하는 것은 미들웨어로 처리됨. 
         * 2. 삭제를 원하는 이미지를 s3에서 삭제 해야함.
         */
        AWS.config.update(userConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();
        if(typeof updatedURI === 'string'){ // 하나만 수정하고자 하면 배열이 아닌 string으로 넘겨온다. 그래서 따로 처리를 해줘야 해.
            const Key = updatedURI.replace('https://yeomanda-userface.s3.ap-northeast-2.amazonaws.com/', '')
            const params = {
                Bucket : 'yeomanda-userface',
                Key : `${Key}`
            }
            await s3.headObject(params).promise()
            try{
                await s3.deleteObject(params).promise()
            }catch(err){
                console.log("ERROR in file Deleting : " +(err))
            }
        }
        else{
            for(const uri in  updatedURI){
                const Key = updatedURI[uri].replace('https://yeomanda-userface.s3.ap-northeast-2.amazonaws.com/', '')
                const params = {
                    Bucket : 'yeomanda-userface',
                    Key : `${Key}`
                }
                await s3.headObject(params).promise()
                try{
                    await s3.deleteObject(params).promise()
                }catch(err){
                    console.log("ERROR in file Deleting : " +(err))
                }
            }
        }

        /**
         * 3. user db에 변경사항 업데이트
         */
        const params_update2 = {
            Bucket : 'yeomanda-userface',
            Prefix : `${email}`+'/'
        }
        const updatedFilePath = await s3.listObjects(params_update2).promise()
        keysList = []
        updatedFilePath.Contents.filter(async(u) => {
            keysList.push(u.Key)
            if(keysList.length === updatedFilePath.Contents.length){
                const params_update = {
                    TableName: userConfig.aws_table_name,
                    Key:{
                        "email": email
                    },
                    UpdateExpression: "set files = :f",
                    ExpressionAttributeValues:{
                        ":f": keysList
                    },
                    ReturnValues:"UPDATED_NEW"
                };
                const result = await docClient.update(params_update).promise()
            }
        })
        return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.UPDATE_PROFILE_SUCCESS))

    }catch(err){
        console.log(err);
        return res
            .status(statusCode.INTERNAL_SERVER_ERROR)
            .send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR));
    }
    


}

module.exports = {
    showFavoriteTeamName,
    deleteFavorite,
    finishTravel,
    showFavoritesDetail,
    getProfile,
    updateProfile
}
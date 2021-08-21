const util = require('../modules/util');
const responseMessage = require('../modules/responseMessage');
const statusCode = require('../modules/statusCode');
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
 * function that getPbjects from s3 with given path.
 */
const getObjectFromS3 = async (list, path) => {
    return new Promise((resolve, reject) => {
        for(var i=0; i<path.length; i++){
            const params = { Bucket: "yeomanda-userface", Key: path[i]}
            s3.getObject(params, function(err, data){
                if(err){
                    console.log('something wrong when get object from the path.');
                    //return res.status(statusCode.OK).send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.NO_FILES_IN_S3))
                }
                else{
                    //const result = Buffer.from(data.Body).toString('utf8');
                    //console.log(result)
                    //fileList.push(result)
                    //res.send({data})
                    list.push(data.Body)
    
                }
            })
        }
        resolve(list)
        //AWS.config.loadFromPath(path[i]);
        
    })
    //for(var i=0; i<path.length; i++){
        const params = { Bucket: "yeomanda-userface", Key: path}
        //AWS.config.loadFromPath(path[i]);
        s3.getObject(params, function(err, data){
            if(err){
                console.log('something wrong when get object from the path.');
                //return res.status(statusCode.OK).send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.NO_FILES_IN_S3))
            }
            else{
                //const result = Buffer.from(data.Body).toString('utf8');
                //console.log(result)
                //fileList.push(result)
                //res.send({data})
                return Buffer.from(data.Body)

            }
        })
    //}
}


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


const userDetail = async (req, res) => {
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
         *  if response query result, password is showed.
         */
        const s3path = checkEmail_from_user.Items[0].files
        const fileList = []
        //getObjectFromS3(fileList, s3path).then()//result => {res.send(result)}).catch(err => {res.send(err)})
        const userResult = {
            'email' : checkEmail_from_user.Items[0].email,
            'birth' : checkEmail_from_user.Items[0].birth,
            'sex' : checkEmail_from_user.Items[0].sex,
            'name' : checkEmail_from_user.Items[0].name,
            'files' : await getObjectFromS3(fileList, s3path).then()
        }
        return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.READ_USER_SUCCESS, userResult))
        //return res.write('buffer', userFace);

    }
}

module.exports = {
    favorite,
    userDetail
}
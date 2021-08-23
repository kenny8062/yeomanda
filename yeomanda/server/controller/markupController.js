const util = require('../modules/util');
const fs = require('fs')
const path = require('path');
const utils = require('util')
const responseMessage = require('../modules/responseMessage');
const statusCode = require('../modules/statusCode');

// dynamodb
const AWS = require('aws-sdk')
const favoriteConfig = require('../config/aws/Favorite')
const userConfig = require('../config/aws/User')


// rds mysql
const mysql_config = require('../config/aws/Travelers'); 
const { get } = require('../routes/markup');
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

const writeFile = utils.promisify(fs.writeFile)

/**
 * function that getPbjects from s3 with given path.
 */
const getObjectFromS3 = async (list, path) => {
    for(var i=0; i<path.length; i++){
        const params = { Bucket: "yeomanda-userface", Key: path[i]}
        s3.getObject(params, async function(err, data){
            if(err){
                console.log('something wrong when get object from the path.');
            }
            else{
                list.push(data.Body)
                if(list.length === path.length){
                    return list
                }
                //Buffer.from(data.Body).toString('utf8')
            }
        })
    }    //AWS.config.loadFromPath(path[i]);
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
        for(var i=0; i<s3path.length; i++){
            const params = { Bucket: "yeomanda-userface", Key: s3path[i]}
            //s3.getObject(params, { stream : true }).promise().then((data) => {
                const readStream = s3.getObject(params).createReadStream();
                console.log(typeof readStream)
                const writeStream = fs.createWriteStream(path.join(__dirname, `../userfaces/${checkEmail_from_user.Items[0].email}_${i}.txt`));
                readStream.pipe(writeStream)
                //xwriteFile('../public/images/test.txt', data.Body)         
                //Buffer.from(data.Body).toString('utf8')     
        }
        // s3path.forEach((i) => {
        //     const params = { Bucket: "yeomanda-userface", Key: i}
        //     s3.getObject(params, async function(err, data){
        //         fileList.push(data.Body)
        //         console.log(fileList)
        //     })

        // })
        //var result = await getObjectFromS3(fileList, s3path)
        
        //getObjectFromS3(fileList, s3path).then()//result => {res.send(result)}).catch(err => {res.send(err)})
        var userfaces = []
        fs.readFile(path.join(__dirname, '../userfaces/test.txt'), (err, data) => {
            if (err) {
              console.error(err)
            }
            console.log(data)
            //userfaces.push(data)
        })
        console.log(userfaces)
        //const userfaces = fs.readFileSync(`../public/userfaces/${checkEmail_from_user.Items[0].email}_0.txt`, 'utf8')
        const userResult = {
            'email' : checkEmail_from_user.Items[0].email,
            'birth' : checkEmail_from_user.Items[0].birth,
            'sex' : checkEmail_from_user.Items[0].sex,
            'name' : checkEmail_from_user.Items[0].name,
            'files' : userfaces
        }
        return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.READ_USER_SUCCESS, userResult))
        //return res.write('buffer', userFace);

    }
}

module.exports = {
    favorite,
    userDetail
}
const util = require('../modules/util');
const responseMessage = require('../modules/responseMessage');
const statusCode = require('../modules/statusCode');
const bcrypt = require('bcryptjs');
const jwt = require('../modules/jwt')
const AWS = require('aws-sdk')
const userConfig = require('../config/aws/User')
const emailValidator = require("email-validator");
const fcmConfig = require('../config/aws/FcmToken')
/**
 * connection to aws mysql server
 */
const mysql = require("mysql2/promise");
const conn = require('../config/aws/Travelers');

const signup = async(req, res) => {
    try{
        const { email, password, name, birth, sex } = req.body; // 앞전에 미들웨어를 통해서 file은 사전에 처리되어 여기서는 req에 file이 없다. 
        if(!email || !password || !name || !birth || !sex){
            return res.status(statusCode.OK).send(util.fail(statusCode.BAD_REQUEST, responseMessage.NULL_VALUE));
        }   
        if(typeof email != 'string' || typeof password != 'string' || typeof name != 'string' || typeof birth != 'string' || typeof sex !='string'){
            return res.status(statusCode.OK).send(util.fail(statusCode.BAD_REQUEST, responseMessage.OUT_OF_VALUE));
        }
        if(!emailValidator.validate(email)){
            return res.status(statusCode.OK).send(util.fail(statusCode.BAD_REQUEST, responseMessage.WRONG_VALIDATION));
        }
        /**
         * hash password
         */
        const salt = await bcrypt.genSalt(10)
        const saltedPassword = await bcrypt.hash(password, salt)
        /**
         * receive user face image  
         */
        const fileKey = req.files.map((files) => {
            return files.key;
        })
        /**
         * connect to aws dynamodb
         */
        AWS.config.update(userConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();
        const params = {
            TableName : userConfig.aws_table_name,
            Item : {
                email : email,
                password : saltedPassword,
                name : name,
                birth : birth,
                sex : sex,
                files : fileKey
            }
        };
        // 회원가입할 때 이메일 인증을 거치기 때문에 해당 이메일은 고유성을 갖는다 -> 중복 체크 필요 x 
        const result = await docClient.put(params).promise()

        /**
         * fcm_token 테이블에 token 값 null로 더미데이터 삽입 
         */
        const connection = await mysql.createConnection(conn.db_info);
        const sql = `insert into fcm_token(email, token) values ('${email}', 0);` 
        const result_sql = await connection.query(sql)

        return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.SIGN_UP_SUCCESS))
        
    } catch (err) {
        console.log(err);
        return res
            .status(statusCode.OK)
            .send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR));

    }
}

const login = async (req, res) => {
    try{
        const failed = "fail"
        const { email, password, fcm_token } = req.body
        console.log(req.body)
        if(!email || !password) {
            return res.status(statusCode.OK).send(util.fail(statusCode.BAD_REQUEST, responseMessage.NULL_VALUE, {'token' : failed}));
        }

        AWS.config.update(userConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();
        const params = {
            TableName : userConfig.aws_table_name,
            KeyConditionExpression: 'email = :i',
            ExpressionAttributeValues: {
                ':i' : email
            }
            
        };
        const checkEmail = await docClient.query(params).promise()

        if(checkEmail.Items.length ===0){
            return res.status(statusCode.OK).send(util.fail(statusCode.BAD_REQUEST, responseMessage.NO_USER, {'token' : failed}));
        }
        const checkPw = await bcrypt.compare(password, checkEmail.Items[0].password)
        if(!checkPw){
            return res.status(statusCode.OK).send(util.fail(statusCode.BAD_REQUEST, responseMessage.MISS_MATCH_PW, {'token' : failed}));
        }
        const token = await jwt.sign(checkEmail);

        /**
         * fcm token을 찾아보고, 만약 있다면?
         * 그때 email을 확인해봐 -> 달라? 다른 사람이 로그인했다는 말.
         * 다르면, token을 null 처리하고, 맞는 email에 지금 token으로 업데이트 
         * 없다면? 업데이트
         */
        const connection = await mysql.createConnection(conn.db_info);
        const sql = `select email from fcm_token where token = '${fcm_token}';` 
        const result_sql = await connection.query(sql)
        if(result_sql.length){
            if(email !== result_sql[0][0].email){
                const connection_0 = await mysql.createConnection(conn.db_info);
                const sql_0 = `update fcm_token set token = 0 where email = '${result_sql[0][0].email}';` 
                const result_sql_0 = await connection_0.query(sql_0)
                
                const connection_1 = await mysql.createConnection(conn.db_info);
                const sql_1 = `update fcm_token set token = '${fcm_token}' where email = '${email}';` 
                const result_sql_1 = await connection_1.query(sql_1)
                
            }
        }
        else{
            const connection_2 = await mysql.createConnection(conn.db_info);
            const sql_2= `update fcm_token set token = '${fcm_token}' where email = '${email}';` 
            const result_sql_2 = await connection_2.query(sql_2)
        }
        /**
         * 여행계획 세운 사람인지 아닌지 판단 -> 세우지 않았다면 채팅 기능 막아둠
         */
        const connection_3 = await mysql.createConnection(conn.db_info);
        const sql_team_3 = `select * from travel_plan where email = '${email}' and isfinished = 0;`
        const result_plan = await connection_3.query(sql_team_3)
        if(result_plan.length){
            return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.SIGN_IN_SUCCESS, {'token' : token, 'hasPlanned' : true}));
        }
        else{
            return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.SIGN_IN_SUCCESS, {'token' : token, 'hasPlanned' : false}));
        }
    } catch (err) {
        console.log(err);
        return res
            .status(statusCode.INTERNAL_SERVER_ERROR)
            .send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR));
    }
}

module.exports = {
    signup,
    login
};
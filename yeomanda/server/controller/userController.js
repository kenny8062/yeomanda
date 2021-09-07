const util = require('../modules/util');
const responseMessage = require('../modules/responseMessage');
const statusCode = require('../modules/statusCode');
const bcrypt = require('bcryptjs');
const jwt = require('../modules/jwt')
const AWS = require('aws-sdk')
const userConfig = require('../config/aws/User')
const emailValidator = require("email-validator");

const testing = async(req, res) => {
    const { email, password } = req.body
    return res.status(200).send('yes')
}

const signup = async(req, res) => {
    try{
        const { email, password, name, birth, sex} = req.body; // 앞전에 미들웨어를 통해서 file은 사전에 처리되어 여기서는 req에 file이 없다. 
        if(!email || !password || !name || !birth || !sex){
            return res.status(statusCode.BAD_REQUEST).send(util.fail(statusCode.BAD_REQUEST, responseMessage.NULL_VALUE));
        }   
        if(typeof email != 'string' || typeof password != 'string' || typeof name != 'string' || typeof birth != 'string' || typeof sex !='string'){
            return res.status(statusCode.BAD_REQUEST).send(util.fail(statusCode.BAD_REQUEST, responseMessage.OUT_OF_VALUE));
        }
        if(!emailValidator.validate(email)){
            return res.status(statusCode.BAD_REQUEST).send(util.fail(statusCode.BAD_REQUEST, responseMessage.WRONG_VALIDATION));
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
        docClient.put(params, function(err, data){
            if (err) {
                return res.status(statusCode.UNAUTHORIZED).send(util.fail(statusCode.UNAUTHORIZED, responseMessage.SIGN_UP_FAIL))
            } else {
                return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.SIGN_UP_SUCCESS))
            }
        })

    } catch (err) {
        console.log(err);
        return res
            .status(statusCode.INTERNAL_SERVER_ERROR)
            .send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR));

    }
}

const login = async (req, res) => {
    try{

        const failed = "fail"
        const { email, password } = req.body
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
        return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.SIGN_IN_SUCCESS, {'token' : token}));
    } catch (err) {
        console.log(err);
        return res
            .status(statusCode.INTERNAL_SERVER_ERROR)
            .send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR));
    }
}

module.exports = {
    signup,
    login,
    testing
};
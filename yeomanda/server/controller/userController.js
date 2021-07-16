const util = require('../modules/util');
const responseMessage = require('../modules/responseMessage');
const statusCode = require('../modules/statusCode');
const bcrypt = require('bcryptjs');
const jwt = require('../modules/jwt')
const AWS = require('aws-sdk')
const userConfig = require('../config/dynamodb/User')

const signup = async(req, res) => {
    try{
        const { email, password, name, birth } = req.body;
        if(!email || !password || !name || !birth){
            res.status(statusCode.BAD_REQUEST).send(util.fail(statusCode.BAD_REQUEST, responseMessage.NULL_VALUE));
        }   

        /**
         * hash password
         */
        const salt = await bcrypt.genSalt(10)
        const saltedPassword = await bcrypt.hash(password, salt)

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
                birth : birth
            }
        };
        console.log(params)
        console.log(docClient)
        docClient.put(params, function(err, data){
            if (err) {
                res.status(statusCode.UNAUTHORIZED).send(util.fail(statusCode.UNAUTHORIZED, responseMessage.SIGN_UP_FAIL))
            } else {
                const { user } = data
                res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.SIGN_UP_SUCCESS, user))
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
        const { email, password } = req.body
        if(!email || !password) {
            res.status(statusCode.BAD_REQUEST).send(statusCode.BAD_REQUEST, responseMessage.NULL_VALUE)
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
        if(!checkEmail){
            res.status(statusCode.BAD_REQUEST).send(statusCode.BAD_REQUEST, responseMessage.NO_USER)
        }
        const checkPw = await bcrypt.compare(password, checkEmail.Items[0].password)
        if(!checkPw){
            res.status(statusCode.BAD_REQUEST).send(statusCode.BAD_REQUEST, responseMessage.MISS_MATCH_PW)
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
    login
};
  

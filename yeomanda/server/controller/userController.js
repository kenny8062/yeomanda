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

module.exports = {
    signup,
};
  

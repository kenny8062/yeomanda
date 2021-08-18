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


const favorite = async (req, res) => {
    const { email, favorite_team_no } = req.body
    const date = Date.now().toString()
    AWS.config.update(favoriteConfig.aws_iam_info);
    const docClient = new AWS.DynamoDB.DocumentClient();
    const params = {
        TableName : favoriteConfig.aws_table_name,
        Item : {
            date : date,
            email : email,
            favorite_team_no : favorite_team_no
        }
    };
    docClient.put(params, function(err, data){
        if (err) {
            return res.status(statusCode.UNAUTHORIZED).send(util.fail(statusCode.UNAUTHORIZED, responseMessage.SIGN_UP_FAIL))
        } else {
            return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.ADD_FAVORITE))
        }
    })


}


module.exports = {
    favorite,
}
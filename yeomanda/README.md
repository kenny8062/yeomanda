### STACK

##### database : aws dynamodb 
- to connect aws remotely, accesskey, secretkey and region should involved in configuration data.
- dynamodb does not support validator.
- hased password is not longer than 50 in dynamodb

``` javascript
const AWS = require('aws-sdk')
const userConfig = require('../config/aws/User')

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
```

../config/aws/User

```javascript
module.exports = {
    aws_table_name : 'USER',
    aws_local : {
        region : 'ap-northeast-2',
        endpoint : 'http://localhost:8000'  
    },
    aws_iam_info : {
        accessKeyId : '~~~~~',
        secretAccessKey : '~~~~~',
        region : 'ap-northeast-2'
    },
}
```

##### image store : s3 
- s3 cannot store file bigger than 400KB

##### test code : mocha
- testcode directory

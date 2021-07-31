### STACK

##### database 
1. <strong>AWS dynamodb</strong>
- to connect aws remotely, accesskey, secretkey and region should involved in configuration data.
- dynamodb does not support validator.
- <strong>hased password is not longer than 50 in dynamodb</strong>

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

2. <strong>AWS RDS - mysql</strong>
- for storing traveler's information to show to other travelers who is located closely
- It is supported for free tier
- designed table for each traveler
- database ide : "tableplus"

- "travel_with" TABLE

|email(pk)|location_gps|team_no|travelDate|isfinished|
|---|---|---|---|---|
|temp0@temp0.com|999111999|0|2020/01/02~2020/01/30|0|
|temp1@temp1.com|999111999|0|2020/01/02~2020/01/30|0|
|temp2@temp2.com|999111999|0|2020/01/02~2020/01/30|0|

게시글은 다른 사람들에게 알리기 위해 작성하기 때문에 여행 동반자 중에 한명만 작성한다. 또한 작성할 때, 낯선이와 놀기를 희망하는 사람들의 email을 함께 작성하여 그 인원들은 같은 team_no로 처리한다. 위와 같이 team_no 0번으로 처리된 동반자들이 이번 여행에 함께 온 사람들인 것을 알 수 있다. 


##### image store : s3 
- dynamdb cannot store file bigger than 400KB
- store user image in s3 that connected with user's email
- store image by middleware
- <https://github.com/jjmmll0727/yeomanda/blob/main/yeomanda/server/middlewares/uploadS3.js>


##### test code : mocha
- testcode directory
- <https://github.com/jjmmll0727/yeomanda/blob/main/yeomanda/server/testcode/user/index.spec.js>


### 작업 문서화 [https://www.notion.so/api-route-ec09aa688f9e46aa8534c96ebff7370c]
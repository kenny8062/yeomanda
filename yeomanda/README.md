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

2. AWS RDS - mysql
- for storing traveler's information to show to other travelers who is located closely
- It is supported for free tier
- designed table for each traveler
- database ide : "tableplus"

![ex_screenshot](./img/inbound.png)
여러 reference를 찾아보면 이 부분을 놓치고 있다. 반드시 인바운드 규칙에서 내 ip로 들어오는것을 허락해줘야 한다. <br>
또한 처음에 rds를 통해서 데이터베이스를 만들고 나서 tabelplus로 접근하고자 하면 aws에서 만든 데이터베이스이름으로 접속이 불가능하다. <br>
aws에서 만든 데이터베이스는 단지 데이터베이스 서버를 만든 것이고, 우리가 실제 sql을 구현하는 데이터베이스는 아직 만들어지지 않은 상태이다. 그렇기 때문에, <br><br>
`show databases;` 
![ex_screenshot](./img/showdatabases.png) 
와 같은 결과가 생긴다. 즉, `create database <database_name>' 와 같이 새롭게 database를 만들어줘야 한다. 

##### image store : s3 
- dynamdb cannot store file bigger than 400KB
- store user image in s3 that connected with user's email
- store image by middleware
- <https://github.com/jjmmll0727/yeomanda/blob/main/yeomanda/server/middlewares/uploadS3.js>


##### test code : mocha
- testcode directory
- <https://github.com/jjmmll0727/yeomanda/blob/main/yeomanda/server/testcode/user/index.spec.js>

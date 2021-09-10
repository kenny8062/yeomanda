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

- "USER" TABLE

|email(pk, String)|birth(String)|files(String, s3 stored location)|name(String)|password(String, encrypted)|sex(String)|
|---|---|---|---|---|---|
|temp0@temp0.com|960727|[{ "S" : "temp0@temp0.com/apply.jpeg" }]|temp0|fe4972@fn2%fnw|M|
|temp1@temp1.com|960727|[{ "S" : "temp1@temp1.com/apply.jpeg" }]|temp1|28yhk2hid$#|M|
|temp2@temp2.com|960727|[{ "S" : "temp2@temp2.com/apply.jpeg" }]|temp2|iou87GI8H@#dwd2|M|

- "MARKUP_FAVORITE" TABLE

|email(String)|favorite_team_no(LIST)|
|---|---|
|temp0@temp0.com|[1,2,3]|
|temp1@temp1.com|[10,11,12]|

2. <strong>AWS RDS - mysql</strong>
- for storing traveler's information to show to other travelers who is located closely
- It is supported for free tier
- designed table for each traveler
- database ide : "tableplus"
- `sudo mysql -h database-yeomanda.cqriqomipg3n.ap-northeast-2.rds.amazonaws.com -p travelers -u jaymee -p`
- jaymeedev




- "travel_with" TABLE

|email(pk, String)|location_gps(String)|team_no(int)|travelDate(String)|isfinished(int)|
|---|---|---|---|---|
|temp0@temp0.com|99.911/19.99|0|2020/01/02~2020/01/30|0|
|temp1@temp1.com|99.911/19.99|0|2020/01/02~2020/01/30|0|
|temp2@temp2.com|99.911/19.99|0|2020/01/02~2020/01/30|0|

게시글은 다른 사람들에게 알리기 위해 작성하기 때문에 여행 동반자 중에 한명만 작성한다. 또한 작성할 때, 낯선이와 놀기를 희망하는 사람들의 email을 함께 작성하여 그 인원들은 같은 team_no로 처리한다. 위와 같이 team_no 0번으로 처리된 동반자들이 이번 여행에 함께 온 사람들인 것을 알 수 있다. 


##### image store : s3 
- dynamdb cannot store file bigger than 400KB
- store user image in s3 that connected with user's email
- store image by middleware
- <https://github.com/jjmmll0727/yeomanda/blob/main/yeomanda/server/middlewares/uploadS3.js>


##### test code : mocha
- testcode directory
- <https://github.com/jjmmll0727/yeomanda/blob/main/yeomanda/server/testcode/user/index.spec.js>
- function(done) -> 해당 함수를 비동기 테스트로 인지하고, 비동기 로직이 완료되면 done() 을 실행함으로써, 테스트가 완료된다. 
- done()을 실행해주지 않으면 2000ms 이후에 타임아웃 실패로 간주한다. 

> notice!!

test code should be really simple so that, do not need to require gitignore file.


### SKILL
##### jwt token
- payload 를 잘 맞춰야 한다. 
- jwt sign 함수로 들어오는 파라미터를 잘 파악해야 한다. 


##### reverse geocoding api
- get location informatin from latitude & logitude 
- use google api
- `global_code` is a 4 character area code and 6 character or longer local code (849VCWC8+R9)
- `compound_code` is a 6 character or longer local code with an explicit location (CWC8+R9, Mountain View, CA, USA).


### CI/CD
##### github action
- .github/workflows
- to connect to ec2 instance `✘ ijaemin@ijaemins-MBP  ~/Desktop  ssh -i "yeo.pem" ubuntu@ec2-3-34-187-47.ap-northeast-2.compute.amazonaws.com`
- to get private ssh key --> `vi path_to_key_pair/[ssh_key].pem` at local --> secrets --> used to connect to ec2 instance
- to generate rsa key pair --> `ssh-keygen -t rsa && cat ~/.ssh/id_rsa.pub` at lunix server --> deploy key 
- private key format should include ----BEGIN ~ END ----
- host name : `ec2-3-34-187-47.ap-northeast-2.compute.amazonaws.com`
- user name : ubuntu
- `ssh: connect to host *** port 22: Connection timed out` -> connect to ec2 instance fail

- nohup : 리눅스에서 프로세스를 실행한 터미널의 세션 연결이 끊어지더라도 지속적으로 동작 할 수 있게 해주는 명령어
- `nohup`[process] &

```shell
stop_list=$(ps -ef | grep "node ./bin/www" | awk '{print $2}') 
for stop_target in ${stop_list};do 
        kill -9 $stop_target 
done 

cd ./repo/yeomanda 
git pull origin main 
cd yeomanda/server 
npm install 
npm run start 
```

***
***

shell scripts do not operate cd, git command, So.
>ssh -o StrictHostKeyChecking=no -i private_key ${USER_NAME}@${HOST_NAME} '
            pm2 stop www
            pm2 delete www
            pm2 kill
            cd ./repo/yeomanda
            git pull origin main
            cd yeomanda/server
            pm2 start ./bin/www
          '



> ISSUE
1. test code should be simple
test code에서 gitignore file 을 열람한다면, CI 과정의 테스트에서 오류를 범한다.(github action이 gitignore file을 열람하지 못하기 때문.)<br>
그렇기 때문에 테스트 코드는 최대한 심플해야 한다. <br>




### 작업 문서화 [https://www.notion.so/api-route-ec09aa688f9e46aa8534c96ebff7370c]

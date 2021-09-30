## STACK
<br><br>

> #### express server

***

<br>

> #### database 

1. <strong>AWS dynamodb</strong>
- to connect aws remotely, accesskey, secretkey and region should involved in configuration data.
- dynamodb does not support validator.
- <strong>hased password is not longer than 50 in dynamodb</strong>

<br>

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

- "chatroom" TABLE

|room_id(PK)|teams|messages|members|

```js
{
  "room_id": {
    "S": "1_5"
  },
  "teams": {
    "L": [
      {
        "S": "sexy"
      },
      {
        "S": "party"
      }
    ]
  },
  "messages": {
    "L": [
      {
        "M": {
          "creates_at": {
            "S": "2109181526"
          },
          "sender": {
            "S": "aaa0@aaa0.com"
          },
          "content": {
            "S": "hi hello"
          }
        }
      },
      {
        "M": {
          "creates_at": {
            "S": "2109181530"
          },
          "sender": {
            "S": "aaa1@aaa1.com"
          },
          "content": {
            "S": "nice to meet you"
          }
        }
      }
    ]
  },
  "members": {
    "L": [
      {
        "S": "aaa0@aaa0.com"
      },
      {
        "S": "aaa1@aaa1.com"
      },
      {
        "S": "aaa2@aaa2.com"
      }
    ]
  }
}
```

<br><br>

2. <strong>AWS RDS - mysql</strong>
- for storing traveler's information to show to other travelers who is located closely
- It is supported for free tier
- designed table for each traveler
- database ide : "tableplus"
- `sudo mysql -h [rds database endpoint] -p travelers -u jaymee -p`
- jaymeedev


<br>

- "travel_with" TABLE

|email(pk, String)|location_gps(String)|team_no(int)|travelDate(String)|isfinished(int)|
|---|---|---|---|---|
|temp0@temp0.com|99.911/19.99|0|2020/01/02~2020/01/30|0|
|temp1@temp1.com|99.911/19.99|0|2020/01/02~2020/01/30|0|
|temp2@temp2.com|99.911/19.99|0|2020/01/02~2020/01/30|0|

게시글은 다른 사람들에게 알리기 위해 작성하기 때문에 여행 동반자 중에 한명만 작성한다. 또한 작성할 때, 낯선이와 놀기를 희망하는 사람들의 email을 함께 작성하여 그 인원들은 같은 team_no로 처리한다. 위와 같이 team_no 0번으로 처리된 동반자들이 이번 여행에 함께 온 사람들인 것을 알 수 있다. 

***

<br>

> #### image store : s3 

- dynamdb cannot store file bigger than 400KB
- store user image in s3 that connected with user's email
- store image by middleware
- <https://github.com/jjmmll0727/yeomanda/blob/main/yeomanda/server/middlewares/uploadS3.js>

***
<br>

> #### test code : mocha

- testcode directory
- <https://github.com/jjmmll0727/yeomanda/blob/main/yeomanda/server/testcode/user/index.spec.js>
- function(done) -> 해당 함수를 비동기 테스트로 인지하고, 비동기 로직이 완료되면 done() 을 실행함으로써, 테스트가 완료된다. 
- done()을 실행해주지 않으면 2000ms 이후에 타임아웃 실패로 간주한다. 

<br><br>
<strong>notice!!</strong>

test code should be really simple so that, do not need to require gitignore file.

<br><br><br>

## SKILL
> #### jwt token

- payload 를 잘 맞춰야 한다. 
- jwt sign 함수로 들어오는 파라미터를 잘 파악해야 한다. 
***
<br>

> #### reverse geocoding api

- get location informatin from latitude & logitude 
- use google api
- `global_code` is a 4 character area code and 6 character or longer local code (849VCWC8+R9)
- `compound_code` is a 6 character or longer local code with an explicit location (CWC8+R9, Mountain View, CA, USA).
***
<br>

> #### socketio

version issue
- nodejs - 4.2.0
- android - 2.0.0


***
<br>

> #### pm2

``` js
module.exports = {
  apps: [{
    name: 'www',
    script: './bin/www',
    instances: 0,
    exec_mode: 'cluster'
  }]
}
```

무중단 서비스 및 클러스터링 
[https://engineering.linecorp.com/ko/blog/pm2-nodejs/](https://engineering.linecorp.com/ko/blog/pm2-nodejs/)
***
<br>

> #### redis

- connect socket with clustered pm2 server
- without it, cannot connect with various socket that distributed to varios process in single thread nodejs server
- pm2 clustering 과 socketio를 통합하기 위해서는 `redis server`가 먼저 실행되어야 한다. 
- how to start and stop redis server
- [https://tableplus.com/blog/2018/10/how-to-start-stop-restart-redis.html](https://tableplus.com/blog/2018/10/how-to-start-stop-restart-redis.html)
- 캐시를 사용하여 메세지들을 저장하고 한꺼번에 디비에 저장할 수 있다. 
- 리눅스에서 돌렸을 때는 하나의 프로세스만 사용하더라... 잘 안되는건가...
***

<br>





### CI/CD
> #### github action

- .github/workflows
- to connect to ec2 instance `✘ ijaemin@ijaemins-MBP  ~/Desktop  ssh -i "yeo.pem" ubuntu@ec2-3-34-187-47.ap-northeast-2.compute.amazonaws.com`
- to get private ssh key --> `vi path_to_key_pair/[ssh_key].pem` at local --> secrets --> used to connect to ec2 instance
- to generate rsa key pair --> `ssh-keygen -t rsa && cat ~/.ssh/id_rsa.pub` at lunix server --> deploy key 
- private key format should include ----BEGIN ~ END ----
- host name : `ec2-3-34-187-47.ap-northeast-2.compute.amazonaws.com`
- user name : ubuntu
- `ssh: connect to host *** port 22: Connection timed out` -> connect to ec2 instance fail
- local에서 install Package 하면 `local`에서 `npm install` 하고 Push 하면 된다 ??? 

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


<br><br>

> ISSUE

1. test code should be simple
test code에서 gitignore file 을 열람한다면, CI 과정의 테스트에서 오류를 범한다.(github action이 gitignore file을 열람하지 못하기 때문.)<br>
그렇기 때문에 테스트 코드는 최대한 심플해야 한다. <br>

2. 가끔 nodemon이 실행되지 않을 때가 있다. 
node_version issue -> `nvm use 12.22.6`

<br><br>

### 작업 문서화 [https://www.notion.so/api-route-ec09aa688f9e46aa8534c96ebff7370c]

const util = require('../modules/util');
const responseMessage = require('../modules/responseMessage');
const statusCode = require('../modules/statusCode');
const axios = require('axios')
const googleapis = require('../config/googleApi')
const AWS = require('aws-sdk')
const userConfig = require('../config/aws/User')
/**
 * connection to aws mysql server
 */
const mysql = require("mysql2/promise");
const conn = require('../config/aws/Travelers');


const getLocation = async(latlng) => {
    try{
        return await axios.get(`https://maps.googleapis.com/maps/api/geocode/json?latlng=${latlng}&key=${googleapis.key}`)
    }catch(err){
        console.log(err);
    };
}

/**
 * grouping function
 */
function groupBy(list, keyGetter) {
    const map = new Map();
    list.forEach((item) => {
         const key = keyGetter(item);
         const collection = map.get(key);
         if (!collection) {
             map.set(key, [item]);
         } else {
             collection.push(item);
         }
    });
    return map;
}


// right after login api
const showTravelers = async(req, res) => {
    try{
        const { latitude, longitude } = req.body
        const connection = await mysql.createConnection(conn.db_info);

        /**
         * find traveler who is closed with login user.
         */
        const latlng = latitude + ',' + longitude
        const locationResult = await getLocation(latlng)
        const country_code = locationResult.data.plus_code.global_code // country code ex)87G8M376+PJ - koera
        const country = country_code.slice(0,4);
        console.log(country)
        /**
         * travel_with table에서 찾는 데이터들에서 이메일을 추출하여, dynamodb user table 에서 pk로 접근하여 name을 뽑아낸다.
         * req에서 받는 데이터를 가지고 현재 사용자의 위치를 찾는데, global code 의 앞 4자리(국가)만을 이용하여 같은 나라에 있는 사용자의 회원정보들을 response 한다.
         */
        const sql = `select * from travel_plan where region_info like '${country}%' and isfinished = '0';` 
        const result = await connection.query(sql)

        await connection.end()

        AWS.config.update(userConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();
    
        /**
         * teams -> 비슷한 지역에 있는 사용자들의 plan들
        "email",
        "location_gps",
        "team_no",
        "travelDate",
        "isfinished",
        "region_info"
            */
    
        var travelers = [] // 여행객들의 정보를 담기 위한 배열
        var teamList = []
        result[0].forEach((e) => {
            teamList.push(e.team_no)
        })
        const teamListNoOverlap = Array.from(new Set(teamList)); // 중복없는 team_no list 
        const grouped = groupBy(result[0], team => team.team_no); // 원하는 key값으로 매핑할 수 있다.
        
        for(var i=0; i<teamListNoOverlap.length; i++){
            var travelersEmail = [] // 근처에 있는 여행객들의 이메일 정보를 담을 배열
            for(var j=0; j<grouped.get(teamListNoOverlap[i]).length; j++){
                travelersEmail.push(grouped.get(teamListNoOverlap[i])[j].email) // 같은 팀의 이메일들이 담긴다.
            }
    
            const nameList = []
            for(var m=0; m<grouped.get(teamListNoOverlap[i]).length; m++){
                /**
                 * 팀별로 잘 묶이긴 하는데 안드로이드에서 넘어온 이메일 데이터
                 * (androidx.appcompat.widget.AppCompatEditText{919eb14 VFED..CL. ........ 0,0-1080,)가 
                 * 이상해서 해당 이메일을 가지고 USER table에서 찾을 수 없어.
                 */
                const params = {
                    TableName : userConfig.aws_table_name,
                    KeyConditionExpression: 'email = :m',
                    ExpressionAttributeValues: {
                        ':m' : grouped.get(teamListNoOverlap[i])[m].email 
                    }
                };
                const result = await docClient.query(params).promise()
                nameList.push(result.Items[0].name)
            }
            
            travelers.push({
                "email" : travelersEmail, 
                "team_no" : teamListNoOverlap[i], 
                "location_gps" : grouped.get(teamListNoOverlap[i])[0].location_gps,
                "travelDate" : grouped.get(teamListNoOverlap[i])[0].travelDate,
                "isfinished" : grouped.get(teamListNoOverlap[i])[0].isfinished,
                "team_name" : grouped.get(teamListNoOverlap[i])[0].team_name,
                "name" : nameList
            })
        }
        return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.QUERY_SUCCESS, travelers))
            
    }catch(err){
        return res.status(statusCode.OK).send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, err))
    }
    
    
}


const registerPlan = async(req, res) => {
    try{
        const connection = await mysql.createConnection(conn.db_info);

        const sql_team_no = 'select max(uniq_no) as newUniq, max(team_no) as newTeam from travel_plan;'
        var new_team_no = 0
        var new_uniq_no = 0
        var success_count = 0
        /**
         * 1. 가장 먼저 팀 갯수를 확인하고
         * 2. 새롭게 팀을 등록하고자 하는데, 먼저 회원인지 아닌지 확인하고,
         * 3. 만약에 이미 등록한 적이 있다면 (디비에서 쿼리문으로 인해서 결과가 확인되면), 등록 못하게 해야한다. 
         * 4. 등록한 적이 없다면 (디비에서 확인 안된다면), 등록하자. 
         */
        const result = await connection.query(sql_team_no)
        new_team_no = result[0][0].newTeam
        new_uniq_no = result[0][0].newUniq
        console.log(new_uniq_no)
        const plan = req.body
        const jsonPlan = JSON.parse(JSON.stringify(plan));

        const location_gps = jsonPlan[0]["latitude"] + ',' + jsonPlan[0]["longitude"]
        const locationResult = await getLocation(location_gps)
        const country = locationResult.data.plus_code.global_code

        // check validation user email in USER
        AWS.config.update(userConfig.aws_iam_info);
        const docClient = new AWS.DynamoDB.DocumentClient();
        
        for(var i=0; i<Object.keys(jsonPlan).length; i++){

            const travelerEmail = jsonPlan[i]["travelMate"].toString()
            const params_to_find_from_email = {
                TableName : userConfig.aws_table_name,
                KeyConditionExpression: 'email = :i',
                ExpressionAttributeValues: {
                    ':i' : travelerEmail
                }
                
            };
            const checkEmail = await docClient.query(params_to_find_from_email).promise()
            
            // 게시판에 등록하려고 하는 여행객이 회원이 아닐 경우
            if(checkEmail.Items.length === 0){
                return res.status(statusCode.OK).
                    send(util.fail(statusCode.BAD_REQUEST, responseMessage.READ_USER_FAIL, "회원 아닌 여행객이 있습니다.")) 
            }

            // // 게시판에 등록한 여행객이 회원일 경우
            const params = {
                uniq_no : (++new_uniq_no).toString(),
                email : travelerEmail,
                location_gps : location_gps,
                team_no : (new_team_no+1).toString(),
                travelDate : jsonPlan[i]["travelDate"].toString(),
                isfinished : 0,
                region_info : country,
                team_name : jsonPlan[i]["teamName"].toString()
            }
            // add new plan
            const sql = 'insert into travel_plan set ?;'  
            const result = await connection.query(sql, params)
                
            success_count = success_count + 1
            if (success_count === Object.keys(jsonPlan).length){
                await connection.end()

                return res.status(statusCode.OK).
                    send(util.success(statusCode.OK, responseMessage.QUERY_SUCCESS, "success to store new plan!!")) 
            }
        }                                
    }catch(err){
        if(err.code = 'ER_DUP_ENTRY'){
            return res.status(statusCode.OK).send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.QUERY_ERROR, 
                "이미 여행중인 여행객이 있습니다."))
        }
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, err))
    }
}


module.exports = {
    showTravelers,
    registerPlan
};
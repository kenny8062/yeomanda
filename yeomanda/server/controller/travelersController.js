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

    
    /**
     * show travelers who is located closely with logged in user.
     * logic for find who is closed,
     * but now, just show all travlers who stored in traveler database.
     */

const mysql_config = require('../config/aws/Travelers');
const conn = mysql_config.init()
mysql_config.connect(conn)

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
    const { latitude, longitude } = req.body
    /**
     * find traveler who is closed with login user.
     */
    const latlng = latitude + ',' + longitude
    const locationResult = await getLocation(latlng)
    const country_code = locationResult.data.plus_code.global_code // country code ex)87G8M376+PJ - koera
    const country = country_code.slice(0,4);
    /**
     * travel_with table에서 찾는 데이터들에서 이메일을 추출하여, dynamodb user table 에서 pk로 접근하여 name을 뽑아낸다.
     * req에서 받는 데이터를 가지고 현재 사용자의 위치를 찾는데, global code 의 앞 4자리(국가)만을 이용하여 같은 나라에 있는 사용자의 회원정보들을 response 한다.
     */
    const sql = `select * from travel_with where region_info like '${country}%';` 
    conn.query(sql, async function(err, teams){
        if(err){
            return res.status(statusCode.BAD_REQUEST).send(util.success(statusCode.OK, responseMessage.QUERY_ERROR, 
                "fail to select * from travel_with where region_info = ?"))
        }
        else{
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
            teams.forEach((e) => {
                teamList.push(e.team_no)
            })
            const teamListNoOverlap = Array.from(new Set(teamList)); // 중복없는 team_no list
            const grouped = groupBy(teams, team => team.team_no); // 원하는 key값으로 매핑할 수 있다.
            
            for(var i=0; i<teamListNoOverlap.length; i++){
                var travelersEmail = [] // 근처에 있는 여행객들의 이메일 정보를 담을 배열
                for(var j=0; j<grouped.get(teamListNoOverlap[i]).length; j++){
                    travelersEmail.push(grouped.get(teamListNoOverlap[i])[j].email)
                }

                const nameList = []
                for(var m=0; m<grouped.get(teamListNoOverlap[i]).length; m++){
                    const params = {
                        TableName : userConfig.aws_table_name,
                        KeyConditionExpression: 'email = :m',
                        ExpressionAttributeValues: {
                            ':m' : grouped.get(teamListNoOverlap[i])[m].email
                        }
                    };
                    console.log(grouped.get(teamListNoOverlap[i]))
                    const result = await docClient.query(params).promise()
                    nameList.push(result.Items[0].name)
                }
                
                travelers.push({
                    "email" : travelersEmail, 
                    "team_no" : teamListNoOverlap[i], 
                    "location_gps" : grouped.get(teamListNoOverlap[i])[0].location_gps,
                    "travelDate" : grouped.get(teamListNoOverlap[i])[0].travelDate,
                    "isfinished" : grouped.get(teamListNoOverlap[i])[0].isfinished,
                    "region_info" : grouped.get(teamListNoOverlap[i])[0].region_info,
                    "name" : nameList
                })
            }
            return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.QUERY_SUCCESS, travelers))
        }
    })
}


const registerPlan = async(req, res) => {
    try{
        const sql_team_no = 'select max(team_no) as newTeam from travel_with;'
        var new_team_no = 0
        var success_count = 0
        /**
         * search how many team has registered 
         */
        conn.query(sql_team_no, async function(err, data){
            if(err){
                console.log(err)
                return res.send('failed to get new team number')
            }
            else{
                new_team_no = data[0].newTeam

                const plan = req.body
                const jsonPlan = JSON.parse(JSON.stringify(plan));

                const location_gps = jsonPlan[0]["latitude"] + ',' + jsonPlan[0]["longitude"]
                const locationResult = await getLocation(location_gps)
                const country = locationResult.data.plus_code.global_code

                for(var i=0; i<Object.keys(jsonPlan).length; i++){
                    // latitude and logitude from request & concat them to location_gps in table field
                    const params = {
                        email : jsonPlan[i]["travelMate"].toString(),
                        location_gps : location_gps,
                        team_no : new_team_no+1,
                        travelDate : jsonPlan[i]["travelDate"].toString(),
                        isfinished : 0,
                        region_info : country
                    }
                    // add new plan
                    const sql = 'insert into travel_with set ?;'
                    conn.query(sql, params, function(err, data){
                        if(err){
                            console.log(err)
                            return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.QUERY_ERROR, "failed to store new plan"))
                        }else{
                            // just for sending response only one time
                            success_count = success_count + 1
                            if (success_count === Object.keys(jsonPlan).length){
                                return res.status(statusCode.OK).
                                    send(util.success(statusCode.OK, responseMessage.QUERY_SUCCESS, "success to store new plan!!")) 
                            }
                        }
                    })
                }
            }
        })
    }catch(err){
        return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.TRY_CATCH_ERROR, "tryCatchError"))
    }
}



module.exports = {
    showTravelers,
    registerPlan
};
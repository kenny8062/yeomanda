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

// right after login api
const showTravelers = async(req, res) => {
    const { latitude, longitude } = req.body
    /**
     * find traveler who is closed with login user.
     */
    const latlng = latitude + ',' + longitude
    const locationResult = await getLocation(latlng)
    const country = locationResult.data.plus_code.global_code // country code ex)87G8M376+PJ - koera
    console.log(country)
    const sql = `select * from travel_with where region_info = '${country}';` // 이 조건 바꿔야 해
    conn.query(sql, async function(err, teams){
        if(err){
            return res.status(statusCode.BAD_REQUEST).send(util.success(statusCode.OK, responseMessage.QUERY_ERROR, "fail"))
        }
        else{
            AWS.config.update(userConfig.aws_iam_info);
            const docClient = new AWS.DynamoDB.DocumentClient();

            const nameList = []
            for(var i=0; i<teams.length; i++){
                console.log(teams[i].email)
                const params = {
                    TableName : userConfig.aws_table_name,
                    KeyConditionExpression: 'email = :i',
                    ExpressionAttributeValues: {
                        ':i' : teams[i].email
                    }
                };
                const checkEmail = await docClient.query(params).promise()   
                console.log(checkEmail.Items[0])          
                const name = checkEmail.Items[i].name.toString()
                nameList.push(name)
            }
            console.log(nameList)
            return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.QUERY_SUCCESS, { teams, nameList }))
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
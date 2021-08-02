const util = require('../modules/util');
const responseMessage = require('../modules/responseMessage');
const statusCode = require('../modules/statusCode');

/**
 * connection to aws mysql server
 */

    
    /**
     * show travelers who is located closely with logged in user.
     * logic for find who is closed,
     * but now, just show all travlers who stored in traveler database.
     */

const mysql_config = require('../config/aws/Travelers');
const { json } = require('express');
const conn = mysql_config.init()
mysql_config.connect(conn)

const showTravelers = async(req, res) => {
    const location_gps = req.body
    const sql = 'select * from travel_with;'
    conn.query(sql, function(err, data){
        if(err){
            return res.send(util.fail(statusCode.INTERNAL_SERVER_ERROR, responseMessage.QUERY_ERROR, "fail"))
        }
        else{
            return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.QUERY_SUCCESS, data))
        }
    })

}
const registerPlan = async(req, res) => {
    const plan = req.body
    const jsonPlan = JSON.parse(JSON.stringify(plan));
    for(var i=0; i<Object.keys(jsonPlan).length; i++){
        const params = {
            email : jsonPlan[i]["travelMate"].toString(),
            location_gps : jsonPlan[i]["location_gps"].toString(),
            team_no : 0,
            travelDate : jsonPlan[i]["travelDate"].toString(),
            isfinished : 0,
        }
        const sql = 'insert into travel_with set ?;'
        conn.query(sql, params, function(err, data){
            if(err){
                return res.send(`failed to store data to database at ${i+1} try`)
            }
        })
    }
    return res.status(statusCode.OK).send(util.success(statusCode.OK, responseMessage.QUERY_SUCCESS, "success to store new plan!!"))
}



module.exports = {
    showTravelers,
    registerPlan
};
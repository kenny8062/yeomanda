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
const conn = mysql_config.init()
mysql_config.connect(conn)

const showTravelers = async(req, res) => {
    const { latitude, logitude } = req.body
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
    try{
        const sql_team_no = 'select max(team_no) as newTeam from travel_with;'
        var new_team_no = 0
        var success_count = 0
        /**
         * search how many team has registered 
         */
        conn.query(sql_team_no, function(err, data){
            if(err){
                console.log(err)
                return res.send('failed to get new team number')
            }
            else{
                new_team_no = data[0].newTeam

                const plan = req.body
                const jsonPlan = JSON.parse(JSON.stringify(plan));
                for(var i=0; i<Object.keys(jsonPlan).length; i++){
                    // latitude and logitude from request & concat them to location_gps in table field
                    const location_gps = jsonPlan[i]["latitude"].toString() + '/' + jsonPlan[i]["logitude"].toString()
                    const params = {
                        email : jsonPlan[i]["travelMate"].toString(),
                        location_gps : location_gps,
                        team_no : new_team_no+1,
                        travelDate : jsonPlan[i]["travelDate"].toString(),
                        isfinished : 0,
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
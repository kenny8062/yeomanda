const util = require('../modules/util');
const responseMessage = require('../modules/responseMessage');
const statusCode = require('../modules/statusCode');

/**
 * connection to aws mysql server
 */
const mysql_config = require('../config/aws/Travelers')
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
    const { location_gps, travelMate, travleDate } = req.body
    
    
    /**
     * show travelers who is located closely with logged in user.
     * logic for find who is closed,
     * but now, just show all travlers who stored in traveler database.
     */

    

    
}


module.exports = {
    showTravelers,
    registerPlan
};
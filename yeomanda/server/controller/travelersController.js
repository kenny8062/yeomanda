const util = require('../modules/util');
const responseMessage = require('../modules/responseMessage');
const statusCode = require('../modules/statusCode');
const bcrypt = require('bcryptjs');
const jwt = require('../modules/jwt')

const showtravelers = async(req, res) => {
    const { location_gps, travelMate, travleDate } = req.body
    
    
    /**
     * show travelers who is located closely with logged in user.
     * logic for find who is closed,
     * but now, just show all travlers who stored in traveler database.
     */

    

    
}


module.exports = {
    showtravelers
};
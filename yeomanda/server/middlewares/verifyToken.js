const jwt = require('../modules/jwt');
const rm = require('../modules/responseMessage');
const sc = require('../modules/statusCode');
const ut = require('../modules/util');
const TOKEN_EXPIRED = -3;
const TOKEN_INVALID = -2;

const verifyToken = {
  checkToken: async (req, res, next) => {
    const token = req.headers.authorization;
    if (!token) {
      return res.status(sc.BAD_REQUEST).send(ut.fail(sc.BAD_REQUEST, rm.EMPTY_TOKEN));
    }
    const user = await jwt.verify(token);

    if (user === TOKEN_EXPIRED) {
      console.log('1')
      return res.status(sc.UNAUTHORIZED).send(ut.fail(sc.UNAUTHORIZED, rm.EXPIRED_TOKEN));
    }
    if (user === TOKEN_INVALID) {
      console.log('2')
      return res.status(sc.UNAUTHORIZED).send(ut.fail(sc.UNAUTHORIZED, rm.INVALID_TOKEN));
    }
    if (user === undefined) {
      console.log('3')
      return res.status(sc.UNAUTHORIZED).send(ut.fail(sc.UNAUTHORIZED, rm.INVALID_TOKEN));
    }
    req.decoded = user;
    next();
  },
};
module.exports = verifyToken;
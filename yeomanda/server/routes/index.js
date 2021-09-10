const express = require('express');
const router = express.Router();
const verifyToken = require('../middlewares/verifyToken');

router.use('/user', require('./user'));
router.use('/travelers', require('./travelers'));
router.use('/markup', verifyToken.checkToken, require('./markup'));
router.use('/menuBar', verifyToken.checkToken, require('./menuBar'));


router.get('/', function(req, res, next) {
    res.render('index', { title: ' yeomanda start !!! ' });
})


module.exports = router;

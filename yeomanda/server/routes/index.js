const express = require('express');
const router = express.Router();
const verifyToken = require('../middlewares/verifyToken');

router.use('/user', require('./user'));
router.use('/travelers', require('./travelers'));
router.use('/markup', require('./markup'));

router.get('/', function(req, res, next) {
    res.render('indexxxx', { title: 'Express' });
});


module.exports = router;
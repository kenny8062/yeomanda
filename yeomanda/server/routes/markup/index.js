const express = require('express');
const router = express.Router();
const markupController = require('../../controller/markupController');

router.get('/favorite/:team_no', markupController.favorite);
router.post('/userDetail', markupController.userDetail);


module.exports = router;
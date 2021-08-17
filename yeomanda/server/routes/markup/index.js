const express = require('express');
const router = express.Router();
const markupController = require('../../controller/markupController');

router.get('/:favorite_team_no', markupController.favorite);


module.exports = router;
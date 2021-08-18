const express = require('express');
const router = express.Router();
const markupController = require('../../controller/markupController');

router.post('/favorite', markupController.favorite);


module.exports = router;
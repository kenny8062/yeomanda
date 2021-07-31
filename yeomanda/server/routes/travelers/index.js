const express = require('express');
const router = express.Router();
const travelersController = require('../../controller/travelersController');

router.post('/showall', travelersController.login);


module.exports = router;
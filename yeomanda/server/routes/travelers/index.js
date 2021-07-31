const express = require('express');
const router = express.Router();
const travelersController = require('../../controller/travelersController');

router.post('/showtravelers', travelersController.showtravelers);


module.exports = router;
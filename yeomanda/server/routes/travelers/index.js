const express = require('express');
const router = express.Router();
const travelersController = require('../../controller/travelersController');

router.post('/showTravelers', travelersController.showTravelers);
router.post('/registerPlan', travelersController.registerPlan);



module.exports = router;
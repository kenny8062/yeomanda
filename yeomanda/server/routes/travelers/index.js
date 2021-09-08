const express = require('express');
const router = express.Router();
const travelersController = require('../../controller/travelersController');

router.post('/showTravelers', travelersController.showTravelers);
router.post('/registerPlan', travelersController.registerPlan);
router.post('/finishTravel', travelersController.finishTravel);



module.exports = router;
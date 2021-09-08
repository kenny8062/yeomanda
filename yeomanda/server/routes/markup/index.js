const express = require('express');
const router = express.Router();
const markupController = require('../../controller/markupController');

router.post('/favorite', markupController.favorite);
router.post('/userDetail', markupController.userDetail);
router.post('/showFavorites', markupController.showFavorites);



module.exports = router;
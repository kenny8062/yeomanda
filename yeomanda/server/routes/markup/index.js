const express = require('express');
const router = express.Router();
const markupController = require('../../controller/markupController');

router.get('/favorite/:team_no', markupController.favorite);
router.post('/userDetail', markupController.userDetail);
router.post('/showFavorites', markupController.showFavorites);
router.get('/deleteFavorite/:team_no', markupController.deleteFavorite);


module.exports = router;
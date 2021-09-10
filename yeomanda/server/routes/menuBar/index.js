const express = require('express');
const router = express.Router();
const menuBarController = require('../../controller/menuBarController');

router.get('/showFavorites', menuBarController.showFavorites);
router.get('/deleteFavorite/:team_no', menuBarController.deleteFavorite);
router.get('/finishTravel', menuBarController.finishTravel);


module.exports = router;
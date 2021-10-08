const express = require('express');
const router = express.Router();
const socketController = require('../../controller/socketController');

router.post('/caching', socketController.caching);



module.exports = router;
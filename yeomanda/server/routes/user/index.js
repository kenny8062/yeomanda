const express = require('express');
const router = express.Router();
const userController = require('../../controller/userController');
const { upload } = require('../../middlewares/uploadS3')

router.post('/signup', upload.array('files'), userController.signup);
router.post('/login', userController.login);


module.exports = router;
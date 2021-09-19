const express = require('express');
const router = express.Router();
const chattingController = require('../../controller/chattingController');

router.get('/getAllMyChatList', chattingController.getAllMyChatList);
router.get('/inToChatRoom/:otherTeamNum', chattingController.inToChatRoom);


module.exports = router;
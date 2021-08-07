var express = require('express');
var router = express.Router();

/* GET users listing. */
// /users/login
router.get('/login', function(req, res, next) {
  res.render('login');
});
// /users/signup
router.get('/signup', function(req, res){
  res.render('signup');
})

router.get('/posts', function(req, res){
  res.render('posts');
})

module.exports = router;

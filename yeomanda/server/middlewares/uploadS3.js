const AWS = require('aws-sdk');
const multer = require('multer')
const multerS3 = require('multer-s3')
const userConfig = require('../config/aws/User')

const s3 = new AWS.S3({
    accessKeyId: userConfig.aws_iam_info.accessKeyId,
    secretAccessKey: userConfig.aws_iam_info.secretAccessKey,
    region : 'ap-northEast-2'
});

const upload = multer({
    storage: multerS3({
      s3: s3,
      bucket: "yeomanda-userface",
      contentType: multerS3.AUTO_CONTENT_TYPE,
      key: (req, file, cb) => {
        cb(null, `upload/${Date.now()}_${file.originalname}`)
      },
      acl: 'public-read',
    })
})

module.exports = {upload,s3};
/**
 * pm2를 이용하여 클러스터링을 구현하기 위한 start option 
 */

module.exports = {
  apps: [{
    name: 'www',
    script: './bin/www',
    instances: 0,
    exec_mode: 'cluster'
  }]
}
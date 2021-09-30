module.exports = {
    apps: [{
    name: 'www',
    script: './bin/www',
    instances: 0,
    exec_mode: 'cluster'
    }]
  }
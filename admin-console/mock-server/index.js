const { runServer } = require('express-mock-server');

const opt_serverConfig = {
  port: 8080
}

const sources = [
  require('./auth/auth.mock')
]

runServer(sources, opt_serverConfig)

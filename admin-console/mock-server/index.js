const { runServer } = require('express-mock-server');

const opt_serverConfig = {
  port: 8080
}

const sources = [
  require('./info/info.mock'),
  require('./health/health.mock'),
  require('./metrics/metrics.mock'),
  require('./config/parameters.mock'),
  require('./auth/auth.mock')
]

runServer(sources, opt_serverConfig)

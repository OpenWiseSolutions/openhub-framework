const { serverStart } = require('express-mock-server')

const optServerConfig = {
  port: 8080
}

const sources = [
  require('./info/info.mock'),
  require('./health/health.mock'),
  require('./metrics/metrics.mock'),
  require('./config/parameters.mock'),
  require('./auth/auth.mock'),
  require('./config/logging.mock'),
  require('./changes/changes.mock'),
  require('./config/environment.mock'),
  require('./errors/errorsOverview.mock'),
  require('./nodes/nodes.mock'),
  require('./config/console.mock'),
  require('./wsdl/wsdl.mock'),
  require('./messages/messages.mock'),
  require('./catalogs/catalogs.mock')
]

serverStart(sources, optServerConfig)

const project = require('../config/project.config')
const jsonServer = require('json-server')

const server = jsonServer.create()
const router = jsonServer.router('./mock-server/db.json')
const middlewares = jsonServer.defaults()

server.use(middlewares)
server.use(project.mock_server_prefix, router)
server.listen(project.mock_server_port, function () {
  console.log('Mock Server is running')
})

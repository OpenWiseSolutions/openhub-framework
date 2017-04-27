module.exports = [
  {
    request: {
      method: 'GET',
      path: '/mgmt/configprops'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify({
        "dataSource": {
          "prefix": "spring.datasource",
          "properties": {
            "maxIdle": 100
          }
        }
      })
    }
  },
  {
    request: {
      method: 'GET',
      path: '/mgmt/env'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify({
        "profiles": [
          "h2",
          "example"
        ],
        "server.ports": {
          "local.server.port": 8080
        },
        "commandLineArgs": {
          "spring.datasource.initialize": "false"
        },
        "systemProperties": {
          "java.runtime.name": "Java"
        },
        "systemEnvironment": {
          "PWD": "false"
        }
      })
    }
  }
]

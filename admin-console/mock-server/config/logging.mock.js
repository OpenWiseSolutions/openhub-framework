module.exports = [
  {
    request: {
      method: 'GET',
      path: '/mgmt/loggers'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify({
        "levels": [
          "OFF",
          "ERROR",
          "WARN",
          "INFO",
          "DEBUG",
          "TRACE"
        ],
        "loggers": {
          "ROOT": {
            "configuredLevel": "INFO"
          },
          "org.openhubframework.openhub": {
            "configuredLevel": "DEBUG"
          },
          "org.netflix1.openhub": {
            "configuredLevel": "ERROR"
          },
          "org.netfli2x.openhub": {
            "configuredLevel": "TRACE"
          },
          "org.facebook.openhub": {
            "configuredLevel": "OFF"
          },
          "org.nohy.openhub": {
            "configuredLevel": "WARN"
          },
          "org.ruky.openhub": {
            "configuredLevel": "ERROR"
          },
          "org.hlava.openhub": {
            "configuredLevel": "DEBUG"
          },
          "org.oko.openhub": {
            "configuredLevel": "ERROR"
          },
          "org.nos.openhub": {
            "configuredLevel": "TRACE"
          },
          "org.palec.openhub": {
            "configuredLevel": "OFF"
          },
          "org.asdf.openhub": {
            "configuredLevel": "WARN"
          },
          "org.fdsa.openhub": {
            "configuredLevel": "ERROR"
          },
          "org.xyz.openhub": {
            "configuredLevel": "OFF"
          },
          "org.abcd.openhub": {
            "configuredLevel": "WARN"
          },
          "org.conejdes.openhub": {
            "configuredLevel": "ERROR"
          },
          "org.ochmonek.openhub": {
            "configuredLevel": "ERROR"
          }
        }
      })
    }
  },
  {
    request: {
      method: 'POST',
      path: '/mgmt/loggers/ROOT'
    },
    response: {
      statusCode: 200
    }
  }
]

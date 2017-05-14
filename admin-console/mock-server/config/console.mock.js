module.exports = [
  {
    request: {
      method: 'GET',
      path: '/api/console-config'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify({
        'config': {
          'menu': {
            'analytisc': {
              'messages': {
                'enable': true
              }
            },
            'infrastructure': {
              'services': {
                'wsdl': {
                  'enable': true
                }
              }
            },
            'cluster': {
              'nodes': {
                'enable': true
              }
            },
            'configuration': {
              'systemParams': {
                'enable': true
              },
              'logging': {
                'enable': true
              },
              'environment': {
                'enable': true
              },
              'errorCodeCatalog': {
                'enable': true
              }
            },
            'externalLinks': {
              'enable': true,
              'items': [
                {
                  'title': 'OpenHub',
                  'link': 'openhub.org'
                }
              ]
            },
            'changes': {
              'enable': true
            }
          }
        }
      })
    }
  }
]

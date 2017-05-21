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
            'analytics': {
              'messages': {
                'enabled': true
              }
            },
            'infrastructure': {
              'services': {
                'wsdl': {
                  'enabled': true
                }
              }
            },
            'cluster': {
              'nodes': {
                'enabled': true
              }
            },
            'configuration': {
              'systemParams': {
                'enabled': true
              },
              'logging': {
                'enabled': true
              },
              'environment': {
                'enabled': true
              },
              'errorCodeCatalog': {
                'enabled': true
              }
            },
            'externalLinks': {
              'enabled': true,
              'items': [
                {
                  'title': 'OpenHub',
                  'link': 'http://openhub.org'
                }
              ]
            },
            'changes': {
              'enabled': true
            }
          }
        }
      })
    }
  }
]

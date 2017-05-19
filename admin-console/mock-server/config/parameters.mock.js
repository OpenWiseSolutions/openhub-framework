module.exports = [
  {
    request: {
      method: 'GET',
      path: '/config-params'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify({
        'paging': {
          'last': true,
          'totalPages': 1,
          'totalElements': 1,
          'size': 20,
          'number': 0,
          'first': true,
          'sort': [
            'null'
          ],
          'numberOfElements': 1
        },
        'data': [
          {
            'id': 12,
            'code': 'ohf.server.localhostUri.check',
            'categoryCode': 'core.server',
            'currentValue': true,
            'defaultValue': true,
            'dataType': 'BOOL',
            'mandatory': false,
            'description': 'enable/disable checking of localhostUri',
            'validationRegEx': '^(spring-ws|servlet).*$'
          },
          {
            'id': 191,
            'code': 'ohf.server.localhostUri.check',
            'categoryCode': 'core.server',
            'currentValue': null,
            'defaultValue': '111',
            'dataType': 'INT',
            'mandatory': false,
            'description': 'enable/disable checking of localhostUri',
            'validationRegEx': '^(spring-ws|servlet).*$'
          },{
            'id': 192,
            'code': 'ohf.server.localhostUri.check',
            'categoryCode': 'core.server',
            'currentValue': null,
            'defaultValue': '111.111',
            'dataType': 'FLOAT',
            'mandatory': false,
            'description': 'enable/disable checking of localhostUri',
            'validationRegEx': '^(spring-ws|servlet).*$'
          },
          {
            'id': 19,
            'code': 'ohf.server.localhostUri.check',
            'categoryCode': 'core.server',
            'currentValue': 'ochmonek',
            'defaultValue': 'nic',
            'dataType': 'STRING',
            'mandatory': true,
            'description': 'enable/disable checking of localhostUri',
            'validationRegEx': '^(spring-ws|servlet).*$'
          },
          {
            'id': 13,
            'code': 'ohf.server.localhostUri.check',
            'categoryCode': 'core.async',
            'currentValue': false,
            'defaultValue': true,
            'dataType': 'BOOL',
            'mandatory': true,
            'description': 'enable/disable checking of localhostUri',
            'validationRegEx': '^(spring-ws|servlet).*$'
          }
        ]
      })
    }
  }
]

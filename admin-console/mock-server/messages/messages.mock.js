module.exports = [
  {
    request: {
      method: 'GET',
      path: '/api/messages'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify(
        {
          "data": [{
            "id": 3,
            "correlationId": "C12345",
            "sourceSystem": "ochmonek",
            "received": "2017-08-09T09:22:02.873Z",
            "processingStarted": "2017-08-09T09:22:03.009Z",
            "state": "OK",
            "serviceName": "dsfsaf",
            "operationName": "asyncHello"
          }, {
            "id": 1,
            "correlationId": "C1234",
            "sourceSystem": "ochmonek",
            "received": "2017-08-09T09:17:11.627Z",
            "processingStarted": "2017-08-09T09:17:11.925Z",
            "state": "OK",
            "serviceName": "dsfsaf",
            "operationName": "asyncHello"
          }], "limit": 100, "totalElements": 2
        }
      )
    }
  },
  {
    request: {
      method: 'GET',
      path: '/api/messages/:id'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify({
        'id': 5232,
        'allowedActions': [
          'RESTART',
          'CANCEL'
        ],
        'correlationId': '20301-2332-1231',
        'processId': '10231-2311-1144',
        'state': 'OK',
        'processingStarted': '2017-05-22T15:20:10',
        'lastChange': '2017-05-22T15:20:10',
        'errorCode': 'E114',
        'failedCount': 3,
        'sourceSystem': 'CRM',
        'received': '2017-05-22T15:20:10',
        'msgTimestamp': '2017-05-22T15:20:10',
        'serviceName': 'HELLO',
        'operationName': 'check',
        'objectId': 'object1',
        'entityType': 'ACCOUNT',
        'funnelValue': 'MSISDN',
        'funnelComponentId': 'componentId',
        'guaranteedOrder': false,
        'excludeFailedState': true,
        'businessError': 'Not enough balance in the account',
        'customData': 'Some custom data as string',
        'parentMsgId': 42,
        'body': '<hel:hello>Hello</hel:hello>',
        'envelope': '<note>\n<to>Tove</to>\n<from>X</from>\n<heading>Reminder</heading>\n<body>Dont forget this weekend!</body>\n</note>',
        'failedDescription': 'Something went terribly wrong',
        'requests': [
          {
            'id': 10233,
            'uri': 'spring-ws:http://helloservice.com',
            'timestamp': '2017-05-22T15:20:10',
            'payload': '<hel:hello>Hello</hel:hello>',
            'response': {
              'timestamp': '2017-05-22T15:20:10',
              'payload': '<hel:hello>Hello</hel:hello>'
            }
          }
        ],
        'externalCalls': [
          {
            'id': 327,
            'state': 'OK',
            'operationName': 'direct:printGreeting',
            'callId': 'CRM_4yEW32321',
            'lastChange': '2017-05-22T15:20:10'
          }
        ]
      })
    }
  },
  {
    request: {
      method: 'POST',
      path: '/api/messages/:id/action'
    },
    response: {
      statusCode: 200,
      body: {
        "result": "OK",
        "resultDescription": "Message in wrong state"
      }
    }
  }
]

module.exports = [
  {
    request: {
      method: 'GET',
      path: '/api/services/wsdl'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify({
        'data': [
          {
            'name': 'helloWorld',
            'wsdl': 'http://www.example.com/ws/helloWorld.wsdl'
          },
          {
            'name': 'byeWorld',
            'wsdl': 'http://www.example.com/ws/byeWorld.wsdl'
          }
        ]
      })
    }
  }
]

module.exports = [
  {
    request: {
      method: 'GET',
      path: '/mgmt/health'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify(
        {
          'status': 'UP',
          'details': {
            'db': {
              'status': 'UP',
              'details': {
                'database': 'H2',
                'hello': 'connection test'
              }
            },
            'diskSpace': {
              'status': 'UP',
              'details': {
                'free': 164883804160,
                'threshold': 10485760,
                'total': 398313103360
              }
            }
          }
        })
    }
  }
]

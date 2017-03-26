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
          'db': {
            'status': 'UP',
            'database': 'H2',
            'hello': 'connection test'
          },
          'diskSpace': {
            'status': 'UP',
            'free': 164883804160,
            'threshold': 10485760,
            'total': 398313103360
          }
        })
    }
  }
]

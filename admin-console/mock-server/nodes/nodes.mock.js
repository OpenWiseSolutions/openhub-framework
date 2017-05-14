module.exports = [
  {
    request: {
      method: 'GET',
      path: '/api/cluster/nodes'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify(
        {
          'data': [
            {
              'code': 'primary',
              'name': 'Prague',
              'description': 'Detail of node',
              'state': 'RUN',
              'id': 12
            },
            {
              'code': 'secondary',
              'name': 'London',
              'description': 'Lorem ipsum',
              'state': 'STOPPED',
              'id': 121
            },
            {
              'code': 'backup',
              'name': 'Frankfurt',
              'description': 'Lorem ipsum',
              'state': 'HANDLES_EXISTING_MESSAGES',
              'id': 111
            }
          ]
        }
      )
    }
  },
  {
    request: {
      method: 'PUT',
      path  : '/api/cluster/nodes/:id'
    },
    response: {
      statusCode: 200
    }
  },
  {
    request: {
      method: 'DELETE',
      path  : '/api/cluster/nodes/:id'
    },
    response: {
      statusCode: 200
    }
  }
]

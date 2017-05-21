module.exports = [
  {
    request: {
      method: 'POST',
      path: '/login',
    },
    response: () => {
      return {
        statusCode: 200
      }
    }
  },
  {
    request: {
      method: 'GET',
      path: '/logout',
    },
    response: () => {
      return {
        statusCode: 200
      }
    }
  },
  {
    request: {
      method: 'GET',
      path: '/api/auth',
    },
    response: {
      statusCode: 200,
      body: JSON.stringify({
        fullName: 'Walter White'
      })
    }
  }
]

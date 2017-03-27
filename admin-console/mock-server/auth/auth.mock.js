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
  }
]

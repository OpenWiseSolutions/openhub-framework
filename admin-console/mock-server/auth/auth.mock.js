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
  }
]

module.exports = [
  {
    request: {
      method: 'POST',
      path: '/web/admin/login',
    },
    response: () => {
      return {
        statusCode: 200
      }
    }
  }
]

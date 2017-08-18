module.exports = [
  {
    request: {
      method: 'GET',
      path: '/api/catalogs/messageState'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify(
        {
          "data": [
            {
              "code": "NEW",
              "description": "NEW"
            },
            {
              "code": "OK",
              "description": "OK"
            },
            {
              "code": "IN_QUEUE",
              "description": "IN_QUEUE"
            },
            {
              "code": "PROCESSING",
              "description": "PROCESSING"
            },
            {
              "code": "PARTLY_FAILED",
              "description": "PARTLY_FAILED"
            },
            {
              "code": "FAILED",
              "description": "FAILED"
            },
            {
              "code": "WAITING",
              "description": "WAITING"
            },
            {
              "code": "WAITING_FOR_RES",
              "description": "WAITING_FOR_RES"
            },
            {
              "code": "POSTPONED",
              "description": "POSTPONED"
            },
            {
              "code": "CANCEL",
              "description": "CANCEL"
            }
          ]
        }
      )
    }
  }
]

module.exports = [
  {
    request: {
      method: 'GET',
      path: '/api/errors-catalog'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify(
        [
          {
            "name": "OFH",
            "codes": [
              {
                "code": "E100",
                "desc": "'unspecified error'",
                "action": "'reprocess message'"
              }
            ]
          },
          {
            "name": "SOME",
            "codes": [
              {
                "code": "E100",
                "desc": "'unspecified error'",
                "action": "'reprocess message'"
              }
            ]
          }
        ]
      )
    }
  }
]

module.exports = [
  {
    request: {
      method: 'GET',
      path: '/mgmt/info'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify({
        'app': {
          'name': 'OpenHub',
          'core': {
            'build': {
              'number': 'c8ddcfc',
              'time': '2016'
            }
          },
          'description': 'OpenHub integration framework',
          'version': 'OpenHub integration framework'
        },
        'git': {
          'branch': 'feature/OHFJIRA',
          'commit': {}
        }
      })
    }
  }
]

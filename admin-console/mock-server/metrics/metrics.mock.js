module.exports = [
  {
    request: {
      method: 'GET',
      path: '/mgmt/metrics'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify({
        'processors': 8,
        'uptime': 1469438,
        'instance.uptime': 1417067,
        'systemload.average': 2.619141,
        'classes': 16995,
        'classes.loaded': 16995,
        'classes.unloaded': 0,
        'mem': 788092,
        'mem.free': 312755,
        'heap': 3728384,
        'heap.committed': 691200,
        'heap.init': 262144,
        'heap.used': 478444,
        'nonheap': 3728384,
        'nonheap.committed': 691200,
        'nonheap.init': 262144,
        'nonheap.used': 478444,
        'threads': 68,
        'threads.peak': 69,
        'threads.daemon': 29,
        'threads.totalStarted': 82,
        'gc.ps_marksweep.count': 0,
        'gc.ps_marksweep.time': 0,
        'gc.ps_scavenge.count': 16,
        'gc.ps_scavenge.time': 222,
        'datasource.primary.active': 5,
        'datasource.primary.usage': 0.25
      })
    }
  }
]

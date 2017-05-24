module.exports = [
  {
    request: {
      method: 'GET',
      path: '/changes',
    },
    response: {
      statusCode: 200,
      body: `An h1 header
============

Paragraphs are separated by a blank line.

2nd paragraph. *Italic*, **bold**. Itemized lists
look like:

  * this one
  * that one
  * the other one
`
    }
  }

]

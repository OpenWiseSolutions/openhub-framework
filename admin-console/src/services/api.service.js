import CreateApi from './CreateApi'

const apiService = new CreateApi({
  base: '..',
  headers: {
    Accept: 'application/json',
    'content-type': 'application/json'
  }
})

export default apiService

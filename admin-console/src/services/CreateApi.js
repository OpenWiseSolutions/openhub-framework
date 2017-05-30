import qs from 'qs'
import { apiInitialised, fetchError, fetchStart, fetchStop } from './api.module'

export default class ApiService {
  constructor ({ base, headers, credentials = 'include' }) {
    this.base = base
    this.headers = headers
    this.credentials = credentials
  }

  http (path, config) {
    const relativePath = path.substr(this.base.length)
    this.store.dispatch(fetchStart(relativePath))
    return fetch(path, config)
      .then(res => {
        if (!res.ok) {
          const error = new Error(res.statusText || res.status)
          error.response = res
          return Promise.reject(error)
        }
        this.store.dispatch(fetchStop(relativePath))
        return this.resolveBody(res, config)
      })
      .catch(error => {
        this.store.dispatch(fetchError(relativePath, error))
        return Promise.reject(error)
      })
  }

  resolveBody (response, { headers }) {
    switch (headers.Accept) {
      case 'application/json':
        return response.text().then((text) => text ? JSON.parse(text) : {})
      case 'text/plain':
        return response.text()
      default:
        return response
    }
  }

  config (override) {
    return {
      headers: this.headers,
      credentials: this.credentials,
      ...override
    }
  }

  connectApiToStore (store) {
    this.store = store
    store.dispatch(apiInitialised(this.base))
  }

  addHeaders (headers) {
    this.headers = { ...this.headers, ...headers }
  }

  getPath (path, query) {
    const qsString = qs.stringify(query)
    return query
      ? `${this.base}${path}?${qsString}`
      : `${this.base}${path}`
  }

  /**
   * Get request
   * @param path
   * @param query
   * @param config
   * @returns { Promise }
   */
  get (path, query, config) {
    const _config = this.config({ method: 'GET', ...config })
    return this.http(this.getPath(path, query), _config)
  }

  /**
   * Post request
   * @param path
   * @param body
   * @param query
   * @param config
   * @returns { Promise }
   */
  post (path, body, query, config) {
    const _config = this.config({
      method: 'POST',
      body: JSON.stringify(body),
      ...config
    })
    return this.http(this.getPath(path, query), _config)
  }

  /**
   * Put request
   * @param path
   * @param body
   * @param query
   * @param config
   * @returns { Promise }
   */
  put (path, body, query, config) {
    const _config = this.config({
      method: 'PUT',
      body: JSON.stringify(body),
      ...config
    })
    return this.http(this.getPath(path, query), _config)
  }

  /**
   * Delete request
   * @param path
   * @param query
   * @param config
   * @returns { Promise }
   */
  remove (path, query, config) {
    const _config = this.config({ method: 'DELETE', ...config })
    return this.http(this.getPath(path, query), _config)
  }
}

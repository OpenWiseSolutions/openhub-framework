import reducer, { LOGIN_TOGGLE, LOGIN_SUCCESS } from '../../../src/common/modules/auth.module'

describe('Auth Reducer', () => {
  it('should return default state', () => {
    const expected = {
      loginModalOpen: false,
      userData: null,
      config: null
    }
    const actual = reducer(undefined, {})
    expect(actual).to.deep.equal(expected)
  })

  it('should toggle login', () => {
    const expected = {
      loginModalOpen: true,
      userData: null,
      config: null
    }
    const actual = reducer(undefined, { type: LOGIN_TOGGLE })
    expect(actual).to.deep.equal(expected)
  })

  it('should recieve login response and set authenticated state', () => {
    const expected = {
      loginModalOpen: false,
      userData: {},
      config: null
    }

    const actual = reducer(undefined, { type: LOGIN_SUCCESS, payload: {} })
    expect(actual).to.deep.equal(expected)
  })
})

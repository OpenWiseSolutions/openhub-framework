import reducer, { LOGIN_TOGGLE, LOGIN } from '../../../src/common/modules/auth.module'

describe('Auth Reducer', () => {
  it('should return default state', () => {
    const expected = {
      loginModalOpen: false,
      authUser: null,
      loginErrors: null,
      config: null
    }
    const actual = reducer(undefined, {})
    expect(actual).to.deep.equal(expected)
  })

  it('should toggle login', () => {
    const expected = {
      loginModalOpen: true,
      authUser: null,
      loginErrors: null,
      config: null
    }
    const actual = reducer(undefined, { type: LOGIN_TOGGLE })
    expect(actual).to.deep.equal(expected)
  })

  it('should recieve login response and set authenticated state', () => {
    const expected = {
      loginModalOpen: false,
      authUser: {},
      loginErrors: null,
      config: null
    }

    const actual = reducer(undefined, { type: LOGIN, payload: {} })
    expect(actual).to.deep.equal(expected)
  })
})

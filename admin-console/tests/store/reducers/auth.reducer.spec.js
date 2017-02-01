import reducer from '../../../src/common/reducers/auth.reducer'
import { LOGIN_TOGGLE, LOGIN } from '../../../src/common/actions/auth.actions'

describe('Auth Reducer', () => {
  it('should return default state', () => {
    const expected = {
      loginModalOpen: false,
      isAuth: false,
      loginErrors: null
    }
    const actual = reducer(undefined, {})
    expect(actual).to.deep.equal(expected)
  })

  it('should toggle login', () => {
    const expected = {
      loginModalOpen: true,
      isAuth: false,
      loginErrors: null
    }
    const actual = reducer(undefined, { type: LOGIN_TOGGLE })
    expect(actual).to.deep.equal(expected)
  })

  it('should recieve login response and set authenticated state', () => {
    const expected = {
      loginModalOpen: false,
      isAuth: true,
      loginErrors: null
    }

    const actual = reducer(undefined, { type: LOGIN, payload: {} })
    expect(actual).to.deep.equal(expected)
  })

  it('should recieve login response with error', () => {
    const initState = {
      loginModalOpen: true,
      isAuth: false,
      loginErrors: false
    }

    const expected = {
      loginModalOpen: true,
      isAuth: false,
      loginErrors: true
    }

    const actual = reducer(initState, { type: LOGIN, error: true })
    expect(actual).to.deep.equal(expected)
  })
})

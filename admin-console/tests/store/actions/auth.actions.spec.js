import { actions, LOGIN_TOGGLE } from '../../../src/common/modules/auth.module'

describe('Auth actions', () => {
  it('should create an action to toggle login window', () => {
    const expectedAction = {
      type: LOGIN_TOGGLE
    }
    expect(actions.toggleLoginModal()).to.deep.equal(expectedAction)
  })
})

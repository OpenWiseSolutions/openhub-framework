import * as actions from '../../../src/common/actions/auth.actions'

describe('Auth actions', () => {
  it('should create an action to toggle login window', () => {
    const expectedAction = {
      type: actions.LOGIN_TOGGLE
    }
    expect(actions.toggleLoginModal()).to.deep.equal(expectedAction)
  })
})

import * as actions from '../../../src/layouts/CoreLayout/actions/coreLayout.actions'

describe('Core Layout actions', () => {
  it('should create an action to toggle sidebar', () => {
    const expectedAction = {
      type: actions.SIDEBAR_TOGGLE
    }
    expect(actions.toggleSidebar()).to.deep.equal(expectedAction)
  })

  it('should create an action to toggle user menu', () => {
    const expectedAction = {
      type: actions.NAVBAR_USER_TOGGLE
    }
    expect(actions.toggleNavbarUser()).to.deep.equal(expectedAction)
  })
})

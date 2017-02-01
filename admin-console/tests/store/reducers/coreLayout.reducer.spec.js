import reducer from '../../../src/layouts/CoreLayout/reducers/coreLayout.reducer'
import { SIDEBAR_TOGGLE, NAVBAR_USER_TOGGLE } from '../../../src/layouts/CoreLayout/actions/coreLayout.actions'

describe('Core Layout Reducer', () => {
  const defaultState = {
    sidebarExtended: true,
    navbarUserExpanded: false
  }

  it('should return default state', () => {
    const actual = reducer(undefined, {})
    expect(actual).to.deep.equal(defaultState)
  })

  it('should toggle sidebar', () => {
    const expected = {
      sidebarExtended: false,
      navbarUserExpanded: false
    }
    const actual = reducer(defaultState, { type: SIDEBAR_TOGGLE })
    expect(actual).to.deep.equal(expected)
  })

  it('should toggle user menu', () => {
    const expected = {
      sidebarExtended: true,
      navbarUserExpanded: true
    }
    const actual = reducer(defaultState, { type: NAVBAR_USER_TOGGLE })
    expect(actual).to.deep.equal(expected)
  })
})

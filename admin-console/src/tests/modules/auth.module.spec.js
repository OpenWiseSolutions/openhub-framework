// import configureMockStore from 'redux-mock-store'
// import thunk from 'redux-thunk'

import reducer, {
  initialState
} from '../../common/modules/auth.module'

// const mockStore = configureMockStore([thunk])
//
// const fakeResponse = {
//   data: { hello: 'world' }
// }

describe('Catalog Module', () => {
  describe('Reducer', () => {
    test('Should set reducer to initial state', () => {
      const actual = reducer(initialState, {})
      expect(actual).toEqual(initialState)
    })
  })
})

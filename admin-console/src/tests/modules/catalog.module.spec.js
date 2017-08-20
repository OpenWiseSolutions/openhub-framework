import configureMockStore from 'redux-mock-store'
import thunk from 'redux-thunk'

import reducer, {
  SET_CATALOG,
  setCatalog,
  getCatalog,
  initialState
} from '../../common/modules/catalog.module'

const mockStore = configureMockStore([thunk])
const fakeResponse = {
  data: { hello: 'world' }
}

describe('Catalog Module', () => {
  describe('Actions', () => {
    test('setCatalog', () => {
      expect(setCatalog('some', 'data')).toEqual({
        type: SET_CATALOG,
        name: 'some',
        data: 'data'
      })
    })

    test('getCatalog', () => {
      const store = mockStore({
        ...initialState
      })

      const expectedActions = [
        { type: SET_CATALOG, name: 'some_name', data: fakeResponse.data }
      ]

      fetch.mockResponseOnce(JSON.stringify(fakeResponse))

      return store.dispatch(getCatalog('some_name'))
        .then(() => {
          expect(store.getActions()).toEqual(expectedActions)
        })
    })
  })

  describe('Reducer', () => {
    test('Should set reducer to initial state', () => {
      const actual = reducer(initialState, {})
      expect(actual).toEqual(initialState)
    })
  })
})

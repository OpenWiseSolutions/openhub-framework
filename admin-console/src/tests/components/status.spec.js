import React from 'react'
import renderer from 'react-test-renderer'
import { Status } from '../../common/components'

describe('Status Component', () => {
  test('renders status UP', () => {
    const tree = renderer.create(
      <Status status />
    ).toJSON()
    expect(tree).toMatchSnapshot()
  })

  test('renders status DOWN', () => {
    const tree = renderer.create(
      <Status status={false} />
    ).toJSON()
    expect(tree).toMatchSnapshot()
  })
})
